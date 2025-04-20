package com.example.restaurantmanage.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class DashboardData(
    val totalRevenue: Double = 0.0,
    val revenueGrowth: Int = 0,
    val profit: Int = 0,
    val profitGrowth: Int = 0,
    val dailyRevenue: List<Pair<Int, Double>> = emptyList(),
    val loyalCustomers: List<String> = emptyList()
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val orderDao = RestaurantDatabase.getDatabase(application).orderDao()
    private val ratingDao = RestaurantDatabase.getDatabase(application).ratingDao()
    
    private val _data = mutableStateOf(DashboardData())
    val data = _data
    
    init {
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            try {
                // Lấy tất cả đơn hàng
                val allOrders = orderDao.getAllOrders().first()
                
                // Tính tổng doanh thu
                val totalRevenue = allOrders.sumOf { it.totalAmount }
                
                // Tính doanh thu theo ngày (7 ngày gần nhất)
                val dailyRevenue = calculateDailyRevenue(allOrders)
                
                // Tính tốc độ tăng trưởng doanh thu
                val revenueGrowth = calculateRevenueGrowth(allOrders)
                
                // Tính lợi nhuận (giả sử chi phí là 30% doanh thu)
                val profit = (totalRevenue * 0.7).toInt()
                
                // Tính tốc độ tăng trưởng lợi nhuận
                val profitGrowth = calculateProfitGrowth(allOrders)
                
                // Lấy danh sách khách hàng thân thiết (có nhiều đơn hàng nhất)
                val loyalCustomers = findLoyalCustomers(allOrders)
                
                // Cập nhật dữ liệu
                _data.value = DashboardData(
                    totalRevenue = totalRevenue,
                    revenueGrowth = revenueGrowth,
                    profit = profit,
                    profitGrowth = profitGrowth,
                    dailyRevenue = dailyRevenue,
                    loyalCustomers = loyalCustomers
                )
            } catch (e: Exception) {
                // Xử lý lỗi nếu có
                e.printStackTrace()
            }
        }
    }
    
    private fun calculateDailyRevenue(orders: List<OrderEntity>): List<Pair<Int, Double>> {
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("dd", Locale.getDefault())
        val dailyRevenue = mutableMapOf<Int, Double>()
        
        // Lấy 7 ngày gần nhất
        for (i in 7 downTo 1) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_MONTH, -i + 1)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            dailyRevenue[dayOfMonth] = 0.0
        }
        
        // Tính doanh thu theo ngày
        for (order in orders) {
            val orderDate = Date(order.startTime)
            val dayOfMonth = format.format(orderDate).toInt()
            if (dailyRevenue.containsKey(dayOfMonth)) {
                dailyRevenue[dayOfMonth] = dailyRevenue[dayOfMonth]!! + order.totalAmount
            }
        }
        
        // Chuyển đổi sang List<Pair<Int, Double>> và sắp xếp theo ngày
        return dailyRevenue.entries.map { (day, revenue) ->
            day to revenue
        }.sortedBy { it.first }
    }
    
    private fun calculateRevenueGrowth(orders: List<OrderEntity>): Int {
        val currentTime = System.currentTimeMillis()
        val thirtyDaysAgo = currentTime - TimeUnit.DAYS.toMillis(30)
        val sixtyDaysAgo = currentTime - TimeUnit.DAYS.toMillis(60)
        
        // Doanh thu 30 ngày gần đây
        val recentRevenue = orders.filter { it.startTime >= thirtyDaysAgo }
            .sumOf { it.totalAmount }
        
        // Doanh thu 30-60 ngày trước
        val previousRevenue = orders.filter { it.startTime in (sixtyDaysAgo until thirtyDaysAgo) }
            .sumOf { it.totalAmount }
        
        // Tính tốc độ tăng trưởng
        return if (previousRevenue > 0) {
            ((recentRevenue - previousRevenue) / previousRevenue * 100).toInt()
        } else {
            0 // Nếu không có doanh thu trước đó
        }
    }
    
    private fun calculateProfitGrowth(orders: List<OrderEntity>): Int {
        val currentTime = System.currentTimeMillis()
        val thirtyDaysAgo = currentTime - TimeUnit.DAYS.toMillis(30)
        val sixtyDaysAgo = currentTime - TimeUnit.DAYS.toMillis(60)
        
        // Lợi nhuận 30 ngày gần đây (giả sử 70% doanh thu)
        val recentProfit = orders.filter { it.startTime >= thirtyDaysAgo }
            .sumOf { it.totalAmount } * 0.7
        
        // Lợi nhuận 30-60 ngày trước
        val previousProfit = orders.filter { it.startTime in (sixtyDaysAgo until thirtyDaysAgo) }
            .sumOf { it.totalAmount } * 0.7
        
        // Tính tốc độ tăng trưởng
        return if (previousProfit > 0) {
            ((recentProfit - previousProfit) / previousProfit * 100).toInt()
        } else {
            0 // Nếu không có lợi nhuận trước đó
        }
    }
    
    private fun findLoyalCustomers(orders: List<OrderEntity>): List<String> {
        // Đếm số đơn hàng của mỗi khách hàng
        val customerOrderCounts = orders
            .filter { it.customerName.isNotBlank() && it.customerName != "Khách hàng" } // Lọc để loại bỏ khách hàng mặc định
            .groupBy { it.customerName }
            .mapValues { it.value.size }
        
        // Tính tổng giá trị đơn hàng của mỗi khách hàng
        val customerTotalSpending = orders
            .filter { it.customerName.isNotBlank() && it.customerName != "Khách hàng" }
            .groupBy { it.customerName }
            .mapValues { entry -> entry.value.sumOf { it.totalAmount } }
        
        // Kết hợp số lượng đơn hàng và tổng chi tiêu để xác định khách hàng thân thiết
        val loyalCustomers = customerOrderCounts.keys
            .sortedByDescending { customerName ->
                // Điểm số ưu tiên = (Số đơn hàng * 1000) + Tổng chi tiêu
                val orderCount = customerOrderCounts[customerName] ?: 0
                val totalSpending = customerTotalSpending[customerName] ?: 0.0
                orderCount * 1000 + totalSpending
            }
            .take(6)  // Lấy 6 khách hàng hàng đầu
        
        return loyalCustomers
    }
}

// Factory để tạo DashboardViewModel với context
class DashboardViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
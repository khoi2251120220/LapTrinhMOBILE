package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.dao.OrderDao
import com.example.restaurantmanage.data.local.dao.OrderItemDao
import com.example.restaurantmanage.data.local.entity.OrderEntity
import com.example.restaurantmanage.data.local.entity.OrderItemEntity
import com.example.restaurantmanage.data.models.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class OrderViewModel(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao
) : ViewModel() {
    
    private val _currentOrderId = MutableStateFlow<Int?>(null)
    val currentOrderId: StateFlow<Int?> = _currentOrderId.asStateFlow()
    
    private val _orderStatus = MutableStateFlow("")
    val orderStatus: StateFlow<String> = _orderStatus.asStateFlow()
    
    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()
    
    fun getAllOrders(): Flow<List<OrderEntity>> {
        return orderDao.getAllOrders()
    }
    
    fun getOrderById(id: Int): Flow<List<OrderItemEntity>> {
        return orderItemDao.getOrderItemsByOrderId(id)
    }
    
    // Tạo đơn hàng mới từ giỏ hàng
    suspend fun createOrderFromCart(cartItems: List<CartItem>, totalAmount: Double, customerName: String): Int {
        if (cartItems.isEmpty()) {
            throw IllegalStateException("Không thể tạo đơn hàng với giỏ hàng trống")
        }
        
        // Tạo OrderEntity mới
        val order = OrderEntity(
            tableId = null, // Không gắn với bàn cụ thể
            startTime = Date().time,
            totalAmount = totalAmount,
            customerName = customerName, // Lưu tên khách hàng
            status = "COMPLETED" // Trạng thái hoàn thành
        )
        
        // Lưu vào cơ sở dữ liệu
        orderDao.insertOrder(order)
        
        // Lấy ID mới được tạo
        val lastInsertedOrder = orderDao.getLastInsertedOrder()
        val orderId = lastInsertedOrder?.id ?: 0
        
        if (orderId > 0) {
            // Lưu các món ăn trong đơn hàng
            val orderItems = cartItems.map { cartItem ->
                OrderItemEntity(
                    orderId = orderId,
                    menuItemId = cartItem.menuItem.id,
                    quantity = cartItem.quantity
                )
            }
            
            orderItemDao.insertOrderItems(orderItems)
            _currentOrderId.value = orderId
            _customerName.value = customerName
            _orderStatus.value = "COMPLETED"
        }
        
        return orderId
    }
    
    // Lấy tất cả đơn hàng theo trạng thái
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>> {
        return orderDao.getOrdersByStatus(status)
    }
    
    // Cập nhật trạng thái đơn hàng
    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            val order = orderDao.getOrderById(orderId)
            order?.let {
                val updatedOrder = it.copy(status = status)
                orderDao.updateOrder(updatedOrder)
                if (orderId == _currentOrderId.value) {
                    _orderStatus.value = status
                }
            }
        }
    }
    
    // Xóa đơn hàng
    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            val order = orderDao.getOrderById(orderId)
            order?.let {
                orderDao.deleteOrder(it)
                if (orderId == _currentOrderId.value) {
                    _currentOrderId.value = null
                    _orderStatus.value = ""
                }
            }
        }
    }
} 
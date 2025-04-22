package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.dao.CartItemDao
import com.example.restaurantmanage.data.local.dao.MenuItemDao
import com.example.restaurantmanage.data.local.dao.OrderDao
import com.example.restaurantmanage.data.local.dao.OrderItemDao
import com.example.restaurantmanage.data.local.entity.CartItemEntity
import com.example.restaurantmanage.data.local.entity.OrderEntity
import com.example.restaurantmanage.data.local.entity.OrderItemEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Locale

class OrderViewModel(
    private val database: RestaurantDatabase,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val cartItemDao: CartItemDao,
    private val menuItemDao: MenuItemDao
) : ViewModel() {
    
    private val _currentOrderId = MutableStateFlow<String?>(null)
    val currentOrderId: StateFlow<String?> = _currentOrderId
    
    private val _orderStatus = MutableStateFlow("")
    val orderStatus: StateFlow<String> = _orderStatus
    
    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName
    
    // Renamed to avoid name clash with the function below
    val orders = orderDao.getAllOrders()
    val pendingOrders = orderDao.getOrdersByStatus("PENDING")
    
    // User's orders - visible in profile
    private val _userOrders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val userOrders: StateFlow<List<OrderEntity>> = _userOrders

    // Biến để lưu trữ số thứ tự đơn hàng hiện tại
    private val _orderCounter = MutableStateFlow(0)
    
    init {
        // Khởi tạo bộ đếm đơn hàng
        viewModelScope.launch {
            orderDao.getAllOrders().collect { orderList ->
                if (orderList.isNotEmpty()) {
                    // Tìm số lớn nhất trong các mã đơn hàng hiện có
                    val maxOrderNumber = orderList.mapNotNull { order -> 
                        // Trích xuất phần số từ ID đơn hàng
                        order.id.filter { it.isDigit() }.toIntOrNull()
                    }.maxOrNull() ?: 0
                    
                    _orderCounter.value = maxOrderNumber
                }
            }
        }
    }
    
    // This function clashes with the property above, renamed the property
    fun getAllOrders(): Flow<List<OrderEntity>> {
        return orderDao.getAllOrders()
    }
    
    fun getOrderById(id: String): Flow<List<OrderItemEntity>> {
        return orderItemDao.getOrderItemsByOrderId(id)
    }
    
    // Tạo đơn hàng mới từ giỏ hàng
    suspend fun createOrderFromCart(cartItems: List<CartItemEntity>, totalAmount: Double, customerName: String): String {
        if (cartItems.isEmpty()) {
            throw IllegalStateException("Không thể tạo đơn hàng với giỏ hàng trống")
        }
        
        // Tăng bộ đếm đơn hàng
        _orderCounter.value += 1
        
        // Tạo mã đơn hàng với định dạng: số thứ tự
        val orderNumber = _orderCounter.value
        val orderId = orderNumber.toString()
        
        // Tạo đơn hàng và thêm các mục
        val order = OrderEntity(
            id = orderId,
            userId = FirebaseAuth.getInstance().currentUser?.uid,
            customerName = customerName,
            customerEmail = "",
            customerPhone = "",
            orderDate = Date(),
            totalAmount = totalAmount,
            status = "COMPLETED",
            paymentMethod = "",
            tableId = null
        )
        
        orderDao.insertOrder(order)
        
        // Lưu các món ăn trong đơn hàng
        cartItems.forEach { cartItem ->
            val orderItem = OrderItemEntity(
                orderId = orderId,
                menuItemId = cartItem.menuItemId,
                quantity = cartItem.quantity,
                price = cartItem.price,
                notes = cartItem.notes ?: ""
            )
            orderItemDao.insertOrderItem(orderItem)
            
            // Update order count for menu item
            val menuItem = menuItemDao.getMenuItemById(cartItem.menuItemId)
            if (menuItem != null) {
                menuItemDao.updateOrderCount(cartItem.menuItemId, menuItem.orderCount + cartItem.quantity)
            }
        }
        
        // Clear the cart after creating the order
        cartItems.forEach { cartItem ->
            cartItemDao.deleteCartItemByMenuId(cartItem.menuItemId)
        }
        
        // Cập nhật state
        _currentOrderId.value = orderId
        _customerName.value = customerName
        _orderStatus.value = "COMPLETED"
        
        return orderId
    }
    
    // Lấy tất cả đơn hàng theo trạng thái
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>> {
        return orderDao.getOrdersByStatus(status)
    }
    
    // Cập nhật trạng thái đơn hàng
    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            orderDao.updateOrderStatus(orderId, status)
        }
    }
    
    // Xóa đơn hàng
    fun deleteOrder(orderId: String) {
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

    // Load orders for a specific user
    fun loadUserOrders(userId: String) {
        viewModelScope.launch {
            orderDao.getOrdersByUserId(userId).collect { orders ->
                _userOrders.value = orders.sortedByDescending { it.orderDate }
            }
        }
    }
    
    // Load orders for current user
    fun loadCurrentUserOrders() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        currentUserId?.let { userId ->
            loadUserOrders(userId)
        }
    }

    class Factory(private val database: RestaurantDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
                return OrderViewModel(
                    database = database,
                    orderDao = database.orderDao(),
                    orderItemDao = database.orderItemDao(),
                    cartItemDao = database.cartItemDao(),
                    menuItemDao = database.menuItemDao()
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 
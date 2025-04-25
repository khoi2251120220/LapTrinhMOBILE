package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.dao.CartItemDao
import com.example.restaurantmanage.data.local.dao.MenuItemDao
import com.example.restaurantmanage.data.local.entity.CartItemEntity
import com.example.restaurantmanage.data.models.CartItem
import com.example.restaurantmanage.data.models.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch

open class CartViewModel(
    private val cartItemDao: CartItemDao,
    private val menuItemDao: MenuItemDao
) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    open val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _outOfStockItems = MutableStateFlow<List<CartItem>>(emptyList())
    open val outOfStockItems: StateFlow<List<CartItem>> = _outOfStockItems.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    open val total: StateFlow<Double> = _total.asStateFlow()

    private val _totalWithTax = MutableStateFlow(0.0)
    open val totalWithTax: StateFlow<Double> = _totalWithTax.asStateFlow()

    private val _tax = MutableStateFlow(0.0)
    open val tax: StateFlow<Double> = _tax.asStateFlow()

    private val _selectedPayment = MutableStateFlow("Tiền mặt")
    open val selectedPayment: StateFlow<String> = _selectedPayment.asStateFlow()

    private val _paymentMethods = MutableStateFlow(listOf("Tiền mặt", "Thẻ tín dụng", "Ví điện tử"))
    open val paymentMethods: StateFlow<List<String>> = _paymentMethods.asStateFlow()

    private val _isPaymentDropdownExpanded = MutableStateFlow(false)
    open val isPaymentDropdownExpanded: StateFlow<Boolean> = _isPaymentDropdownExpanded.asStateFlow()

    init {
        loadCartItems()
    }

    private suspend fun CartItemEntity.toCartItem(): CartItem {
        val menuItemEntity = menuItemDao.getMenuItemById(menuItemId)
        val menuItem = MenuItem(
            id = menuItemId,
            name = menuItemEntity?.name ?: "",
            price = price,
            categoryId = menuItemEntity?.categoryId ?: 0,
            orderCount = menuItemEntity?.orderCount ?: 0,
            inStock = menuItemEntity?.inStock ?: true,
            image = menuItemEntity?.image ?: "",
            description = menuItemEntity?.description ?: ""
        )
        return CartItem(menuItem, quantity, notes)
    }

    private fun CartItem.toCartItemEntity(): CartItemEntity {
        return CartItemEntity(
            menuItemId = menuItem.id,
            userId = FirebaseAuth.getInstance().currentUser?.uid,
            quantity = quantity,
            price = menuItem.price,
            notes = notes
        )
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            cartItemDao.getAllCartItems()
                .collect { entities -> 
                    val items = entities.map { entity -> entity.toCartItem() }
                    
                    // Separate items into in-stock and out-of-stock
                    val inStockItems = items.filter { it.menuItem.inStock }
                    val outOfStockItems = items.filter { !it.menuItem.inStock }
                    
                    // Update out-of-stock items state
                    _outOfStockItems.value = outOfStockItems
                    
                    // Nhóm các mục giống nhau lại với nhau
                    val groupedItems = inStockItems.groupBy { item -> item.menuItem.id }
                        .map { (_, itemsForId) ->
                            // Tổng hợp số lượng cho cùng một sản phẩm
                            val totalQuantity = itemsForId.sumOf { it.quantity }
                            // Sử dụng ghi chú từ mục đầu tiên
                            val firstItem = itemsForId.first()
                            // Tạo CartItem mới với tổng số lượng
                            CartItem(firstItem.menuItem, totalQuantity, firstItem.notes)
                        }
                    
                    _cartItems.value = groupedItems
                    updateTotal()
                }
        }
    }

    private fun updateTotal() {
        val totalPrice = _cartItems.value.sumOf { it.menuItem.price * it.quantity }
        _total.value = totalPrice
        _tax.value = totalPrice * 0.1
        _totalWithTax.value = totalPrice * 1.1
    }

    fun addToCart(item: MenuItem) {
        viewModelScope.launch {
            // Always check real-time stock status from the database
            val menuItemEntity = menuItemDao.getMenuItemById(item.id)
            if (menuItemEntity == null || !menuItemEntity.inStock) {
                // Item is out of stock - don't add to cart
                return@launch
            }
            
            val currentItems = _cartItems.value.toMutableList()
            val existingItem = currentItems.find { it.menuItem.id == item.id }
            
            if (existingItem != null) {
                // Cập nhật số lượng nếu món ăn đã tồn tại trong giỏ hàng
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
                currentItems[currentItems.indexOf(existingItem)] = updatedItem
                
                // Cập nhật trong database
                val existingEntities = cartItemDao.getCartItemsByMenuId(item.id)
                if (existingEntities.isNotEmpty()) {
                    // Cập nhật mục đầu tiên trong database
                    val firstEntity = existingEntities.first()
                    val updatedEntity = firstEntity.copy(quantity = firstEntity.quantity + 1)
                    cartItemDao.updateCartItem(updatedEntity)
                    
                    // Xóa các mục trùng lặp khác (nếu có)
                    if (existingEntities.size > 1) {
                        existingEntities.drop(1).forEach { entity ->
                            cartItemDao.deleteCartItem(entity)
                        }
                    }
                }
            } else {
                // Thêm món ăn mới vào giỏ hàng
                val newCartItem = CartItem(
                    // Create MenuItem with current stock status
                    menuItem = item.copy(inStock = menuItemEntity.inStock),
                    quantity = 1,
                    notes = null
                )
                currentItems.add(newCartItem)
                cartItemDao.insertCartItem(newCartItem.toCartItemEntity())
            }
            
            _cartItems.value = currentItems
            updateTotal()
        }
    }

    fun setPaymentMethod(method: String) {
        _selectedPayment.value = method
    }

    fun togglePaymentDropdown() {
        _isPaymentDropdownExpanded.value = !_isPaymentDropdownExpanded.value
    }

    fun dismissPaymentDropdown() {
        _isPaymentDropdownExpanded.value = false
    }

    // Add a function to refresh the stock status of all cart items
    fun refreshStockStatus() {
        viewModelScope.launch {
            // Reload all items from the database to get the latest stock status
            loadCartItems()
        }
    }
    
    fun confirmPayment() {
        viewModelScope.launch {
            // First refresh stock status to make sure we only process in-stock items
            refreshStockStatus()
            
            // Only process if there are no out-of-stock items
            if (_outOfStockItems.value.isEmpty()) {
                cartItemDao.clearCart()
                _cartItems.value = emptyList()
                updateTotal()
            }
        }
    }

    // Xóa hoàn toàn một sản phẩm khỏi giỏ hàng
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            // Xóa sản phẩm khỏi database
            cartItemDao.deleteCartItemByMenuId(cartItem.menuItem.id)
            
            // Cập nhật state
            val updatedItems = _cartItems.value.toMutableList()
            updatedItems.removeAll { it.menuItem.id == cartItem.menuItem.id }
            _cartItems.value = updatedItems
            
            updateTotal()
        }
    }
    
    // Giảm số lượng sản phẩm trong giỏ hàng đi 1
    fun decreaseQuantity(cartItem: CartItem) {
        viewModelScope.launch {
            val currentItems = _cartItems.value.toMutableList()
            val existingItem = currentItems.find { it.menuItem.id == cartItem.menuItem.id }
            
            if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    // Giảm số lượng đi 1
                    val updatedItem = existingItem.copy(quantity = existingItem.quantity - 1)
                    currentItems[currentItems.indexOf(existingItem)] = updatedItem
                    
                    // Cập nhật trong database
                    val existingEntities = cartItemDao.getCartItemsByMenuId(cartItem.menuItem.id)
                    if (existingEntities.isNotEmpty()) {
                        val firstEntity = existingEntities.first()
                        val updatedEntity = firstEntity.copy(quantity = firstEntity.quantity - 1)
                        cartItemDao.updateCartItem(updatedEntity)
                    }
                    
                    _cartItems.value = currentItems
                } else {
                    // Nếu số lượng là 1, xóa sản phẩm khỏi giỏ hàng
                    removeFromCart(cartItem)
                }
                
                updateTotal()
            }
        }
    }

    fun removeAllOutOfStockItems() {
        viewModelScope.launch {
            outOfStockItems.value.forEach { item ->
                cartItemDao.deleteCartItemByMenuId(item.menuItem.id)
            }
            _outOfStockItems.value = emptyList()
            loadCartItems()
        }
    }
}
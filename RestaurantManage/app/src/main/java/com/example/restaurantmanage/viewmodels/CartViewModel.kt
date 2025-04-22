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
                    _cartItems.value = items
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
            val currentItems = _cartItems.value.toMutableList()
            val existingItem = currentItems.find { it.menuItem.id == item.id }
            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
                currentItems[currentItems.indexOf(existingItem)] = updatedItem
                cartItemDao.updateCartItem(updatedItem.toCartItemEntity())
            } else {
                val newCartItem = CartItem(item, 1)
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

    fun confirmPayment() {
        viewModelScope.launch {
            cartItemDao.clearCart()
            _cartItems.value = emptyList()
            updateTotal()
        }
    }
}
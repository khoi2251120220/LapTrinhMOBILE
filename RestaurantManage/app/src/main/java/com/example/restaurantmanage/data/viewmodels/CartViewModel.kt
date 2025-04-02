package com.example.restaurantmanage.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.models.CartItem
import com.example.restaurantmanage.data.models.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    private val _totalWithTax = MutableStateFlow(0.0)
    val totalWithTax: StateFlow<Double> = _totalWithTax.asStateFlow()

    private val _tax = MutableStateFlow(0.0)
    val tax: StateFlow<Double> = _tax.asStateFlow()

    private val _selectedPayment = MutableStateFlow("Tiền mặt")
    val selectedPayment: StateFlow<String> = _selectedPayment.asStateFlow()

    private val _paymentMethods = MutableStateFlow(listOf("Tiền mặt", "Thẻ tín dụng", "Ví điện tử"))
    val paymentMethods: StateFlow<List<String>> = _paymentMethods.asStateFlow()

    private val _isPaymentDropdownExpanded = MutableStateFlow(false)
    val isPaymentDropdownExpanded: StateFlow<Boolean> = _isPaymentDropdownExpanded.asStateFlow()

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            val mockItems = listOf(
                CartItem(MenuItem("1", "Nước ép lê", 50000.0, "Nước uống", image = "nuoceple"), 2),
                CartItem(MenuItem("2", "Nước ép dâu", 55000.0, "Nước uống", image = "nuocepdau"), 1)
            )
            _cartItems.value = mockItems
            updateTotal()
        }
    }

    private fun updateTotal() {
        val totalPrice = _cartItems.value.sumOf { it.menuItem.price * it.quantity }
        _total.value = totalPrice
        _tax.value = totalPrice * 0.1 // Thuế 10%
        _totalWithTax.value = totalPrice * 1.1 // Tổng cộng (bao gồm thuế)
    }

    fun addToCart(item: MenuItem) {
        viewModelScope.launch {
            val currentItems = _cartItems.value.toMutableList()
            val existingItem = currentItems.find { it.menuItem.id == item.id }
            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
                currentItems[currentItems.indexOf(existingItem)] = updatedItem
            } else {
                currentItems.add(CartItem(item, 1))
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
            _cartItems.value = emptyList()
            updateTotal()
        }
    }
}
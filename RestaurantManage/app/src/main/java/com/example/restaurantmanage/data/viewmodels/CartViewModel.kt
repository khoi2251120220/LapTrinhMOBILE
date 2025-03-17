package com.example.restaurantmanage.data.viewmodels

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.restaurantmanage.data.models.CartItem
import com.example.restaurantmanage.data.models.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel: ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItem: StateFlow<List<CartItem>> = _cartItems

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    fun addToCart(menuItem: MenuItem){
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.menuItem.id == menuItem.id }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentItems.add(CartItem(menuItem))
        }
        _cartItems.value = currentItems
        calculateTotal()
    }
    private fun calculateTotal(){
        _total.value = _cartItems.value.sumOf { it.menuItem.price * it.quantity }
    }
}
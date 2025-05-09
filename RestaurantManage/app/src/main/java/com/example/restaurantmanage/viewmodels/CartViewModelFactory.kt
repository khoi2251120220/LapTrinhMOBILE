package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantmanage.data.local.RestaurantDatabase

class CartViewModelFactory(private val database: RestaurantDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(
                cartItemDao = database.cartItemDao(),
                menuItemDao = database.menuItemDao()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
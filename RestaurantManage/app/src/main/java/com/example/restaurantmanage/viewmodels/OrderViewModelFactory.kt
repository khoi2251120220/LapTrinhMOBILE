package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantmanage.data.local.RestaurantDatabase

class OrderViewModelFactory(private val database: RestaurantDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderViewModel(
                database.orderDao(),
                database.orderItemDao()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 
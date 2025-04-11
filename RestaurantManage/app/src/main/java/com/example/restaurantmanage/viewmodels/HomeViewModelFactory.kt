package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantmanage.data.local.RestaurantDatabase

class HomeViewModelFactory(private val database: RestaurantDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(database.menuItemDao(), database.categoryDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
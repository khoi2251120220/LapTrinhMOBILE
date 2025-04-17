package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantmanage.data.local.dao.MenuItemDao

class MenuViewModelFactory(private val menuItemDao: MenuItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            return MenuViewModel(menuItemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
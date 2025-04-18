package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.restaurantmanage.data.local.dao.MenuItemDao
import com.example.restaurantmanage.data.local.dao.CategoryDao

class MenuViewModelFactory(
    private val menuItemDao: MenuItemDao,
    private val categoryDao: CategoryDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(menuItemDao, categoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
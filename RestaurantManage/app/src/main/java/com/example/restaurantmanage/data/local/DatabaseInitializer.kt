package com.example.restaurantmanage.data.local

import com.example.restaurantmanage.data.local.dao.CategoryDao
import com.example.restaurantmanage.data.local.dao.MenuItemDao
import com.example.restaurantmanage.data.local.entity.CategoryEntity
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import com.example.restaurantmanage.data.local.entity.categories
import com.example.restaurantmanage.data.local.entity.menuItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object DatabaseInitializer {
    suspend fun initializeData(categoryDao: CategoryDao, menuItemDao: MenuItemDao) {
        // Kiểm tra xem đã có dữ liệu chưa
        val existingCategories = categoryDao.getAllCategories().first()
        if (existingCategories.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                // Thêm categories trước
                categoryDao.insertCategories(categories)
                // Sau đó thêm menu items
                menuItemDao.insertMenuItems(menuItems)
            }
        }
    }
} 
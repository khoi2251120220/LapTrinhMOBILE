package com.example.restaurantmanage.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.models.MenuCategory
import com.example.restaurantmanage.data.models.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val _categories = MutableStateFlow<List<MenuCategory>>(emptyList())
    val categories: StateFlow<List<MenuCategory>> = _categories.asStateFlow()

    private val _topSellingItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val topSellingItems: StateFlow<List<MenuItem>> = _topSellingItems.asStateFlow()

    init {
        loadMenuData()
    }

    private fun loadMenuData() {
        viewModelScope.launch {
            // Mock data - In real app, this would come from a repository
            val mockCategories = listOf(
                MenuCategory(
                    id = 1, 
                    name = "Món phổ biến", 
                    items = listOf(
                        MenuItem(id = "1", name = "Tôm xào chua ngọt", price = 150000.0, category = "Món chính", orderCount = 120),
                        MenuItem(id = "2", name = "Cá hồi nướng", price = 180000.0, category = "Món chính", orderCount = 98),
                        MenuItem(id = "3", name = "Gà nướng", price = 140000.0, category = "Món chính", orderCount = 85, inStock = false)
                    )
                ),
                MenuCategory(
                    id = 2, 
                    name = "Món chính", 
                    items = listOf(
                        MenuItem(id = "4", name = "Bò xào nấm", price = 160000.0, category = "Món chính", orderCount = 75),
                        MenuItem(id = "5", name = "Sườn xào chua ngọt", price = 120000.0, category = "Món chính", orderCount = 82)
                    )
                ),
                MenuCategory(
                    id = 3, 
                    name = "Món tráng miệng", 
                    items = listOf(
                        MenuItem(id = "6", name = "Bánh flan", price = 25000.0, category = "Tráng miệng", orderCount = 150),
                        MenuItem(id = "7", name = "Chè thái", price = 30000.0, category = "Tráng miệng", orderCount = 125)
                    )
                ),
                MenuCategory(
                    id = 4, 
                    name = "Đồ uống", 
                    items = listOf(
                        MenuItem(id = "8", name = "Trà đào", price = 35000.0, category = "Đồ uống", orderCount = 200),
                        MenuItem(id = "9", name = "Cà phê đen", price = 25000.0, category = "Đồ uống", orderCount = 180),
                        MenuItem(id = "10", name = "Sinh tố xoài", price = 40000.0, category = "Đồ uống", orderCount = 90)
                    )
                )
            )
            _categories.value = mockCategories

            // Calculate top selling items
            val topItems = mockCategories.flatMap { it.items }
                .sortedByDescending { it.orderCount }
                .take(5)
            _topSellingItems.value = topItems
        }
    }

    fun updateItemStock(itemId: String, inStock: Boolean) {
        viewModelScope.launch {
            val updatedCategories = _categories.value.map { category ->
                category.copy(
                    items = category.items.map { item ->
                        if (item.id == itemId) item.copy(inStock = inStock) else item
                    }
                )
            }
            _categories.value = updatedCategories
            
            // Update top selling items if needed
            val updatedTopItems = _topSellingItems.value.map { item ->
                if (item.id == itemId) item.copy(inStock = inStock) else item
            }
            _topSellingItems.value = updatedTopItems
        }
    }
} 
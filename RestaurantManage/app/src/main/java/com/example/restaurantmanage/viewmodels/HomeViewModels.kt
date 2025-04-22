package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.dao.CategoryDao
import com.example.restaurantmanage.data.local.dao.CategoryWithMenuItems
import com.example.restaurantmanage.data.local.dao.MenuItemDao
import com.example.restaurantmanage.data.local.entity.CategoryWithItems
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import com.example.restaurantmanage.data.models.MenuCategory
import com.example.restaurantmanage.data.models.MenuItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val database: RestaurantDatabase
) : ViewModel() {
    private val menuItemDao: MenuItemDao = database.menuItemDao()
    private val categoryDao: CategoryDao = database.categoryDao()

    private val _featuredItems = MutableStateFlow<List<MenuItemEntity>>(emptyList())
    val featuredItems: StateFlow<List<MenuItemEntity>> = _featuredItems

    private val _categories = MutableStateFlow<List<CategoryWithItems>>(emptyList())
    val categories: StateFlow<List<CategoryWithItems>> = _categories

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        // Initial data loading
        loadMenuItems()
        loadCategories()
    }

    private fun MenuItemEntity.toMenuItem(): MenuItem {
        return MenuItem(
            id = id,
            name = name,
            price = price,
            categoryId = categoryId,
            orderCount = orderCount,
            inStock = inStock,
            image = image,
            description = description,
            imageResId = 0
        )
    }

    fun loadMenuItems() {
        viewModelScope.launch {
            menuItemDao.getAllMenuItems().collect { items ->
                // Get featured items (items with high order count)
                val featuredItems = items
                    .filter { it.inStock }
                    .sortedByDescending { it.orderCount }
                    .take(6)
                _featuredItems.value = featuredItems
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            categoryDao.getCategoriesWithItems().collect { categoriesWithMenuItems ->
                // Convert CategoryWithMenuItems to CategoryWithItems
                val convertedCategories = categoriesWithMenuItems.map { categoryWithMenuItems ->
                    CategoryWithItems(
                        id = categoryWithMenuItems.category.id,
                        name = categoryWithMenuItems.category.name,
                        description = categoryWithMenuItems.category.description,
                        items = categoryWithMenuItems.items
                    )
                }
                _categories.value = convertedCategories
            }
        }
    }

    fun searchMenuItems(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                loadCategories() // Reset to all categories if query is empty
            } else {
                // Search in menu items
                menuItemDao.searchMenuItems("%$query%").collect { items ->
                    // Create a synthetic category for search results
                    if (items.isNotEmpty()) {
                        val searchCategory = CategoryWithItems(
                            id = 0,
                            name = "Kết quả tìm kiếm",
                            items = items
                        )
                        _categories.value = listOf(searchCategory)
                    } else {
                        _categories.value = emptyList() // No results found
                    }
                }
            }
        }
    }

     fun getFeaturedItems(): List<MenuItem> {
        return _featuredItems.value.map { it.toMenuItem() }
    }

    fun getCategories(): List<MenuCategory> {
        return _categories.value.map { category ->
            MenuCategory(
                id = category.id,
                name = category.name,
                items = category.items.map { it.toMenuItem() }
            )
        }
    }
}
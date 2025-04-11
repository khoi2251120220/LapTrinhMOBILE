package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.dao.CategoryDao
import com.example.restaurantmanage.data.local.dao.MenuItemDao
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import com.example.restaurantmanage.data.models.MenuCategory
import com.example.restaurantmanage.data.models.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

open class HomeViewModel(
    private val menuItemDao: MenuItemDao,
    private val categoryDao: CategoryDao
) : ViewModel() {
    private val _featuredItems = MutableStateFlow<List<MenuItem>>(emptyList())
    open val featuredItems: StateFlow<List<MenuItem>> = _featuredItems.asStateFlow()

    private val _categories = MutableStateFlow<List<MenuCategory>>(emptyList())
    open val categories: StateFlow<List<MenuCategory>> = _categories.asStateFlow()

    init {
        loadData()
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

    private fun loadData() {
        viewModelScope.launch {
            menuItemDao.getAvailableMenuItems()
                .collect { entities ->
                    _featuredItems.value = entities.map { it.toMenuItem() }
                        .sortedByDescending { it.orderCount }
                        .take(4)
                }

            combine(
                categoryDao.getAllCategories(),
                menuItemDao.getAllMenuItems()
            ) { categories, menuItems ->
                val menuItemList = menuItems.map { it.toMenuItem() }
                categories.map { category ->
                    MenuCategory(
                        id = category.id,
                        name = category.name,
                        items = menuItemList.filter { it.categoryId == category.id }
                    )
                }
            }.collect { menuCategories ->
                _categories.value = menuCategories
            }
        }
    }

    fun searchMenuItems(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                menuItemDao.getAvailableMenuItems()
                    .collect { entities ->
                        _featuredItems.value = entities.map { it.toMenuItem() }
                            .sortedByDescending { it.orderCount }
                            .take(4)
                    }

                combine(
                    categoryDao.getAllCategories(),
                    menuItemDao.getAllMenuItems()
                ) { categories, menuItems ->
                    val menuItemList = menuItems.map { it.toMenuItem() }
                    categories.map { category ->
                        MenuCategory(
                            id = category.id,
                            name = category.name,
                            items = menuItemList.filter { it.categoryId == category.id }
                        )
                    }
                }.collect { menuCategories ->
                    _categories.value = menuCategories
                }
            } else {
                menuItemDao.getAllMenuItems()
                    .collect { entities ->
                        val allItems = entities.map { it.toMenuItem() }
                        val filteredItems = allItems.filter {
                            it.name.contains(query, ignoreCase = true) && it.inStock
                        }
                        _featuredItems.value = filteredItems.take(4)

                        combine(
                            categoryDao.getAllCategories(),
                            menuItemDao.getAllMenuItems()
                        ) { categories, menuItems ->
                            val menuItemList = menuItems.map { it.toMenuItem() }
                                .filter { it.name.contains(query, ignoreCase = true) && it.inStock }
                            categories.map { category ->
                                MenuCategory(
                                    id = category.id,
                                    name = category.name,
                                    items = menuItemList.filter { it.categoryId == category.id }
                                )
                            }
                        }.collect { menuCategories ->
                            _categories.value = menuCategories
                        }
                    }
            }
        }
    }

    open fun getFeaturedItems(): List<MenuItem> {
        return _featuredItems.value
    }

    open fun getCategories(): List<MenuCategory> {
        return _categories.value
    }
}
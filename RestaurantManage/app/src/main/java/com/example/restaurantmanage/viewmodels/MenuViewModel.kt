package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.dao.MenuItemDao
import com.example.restaurantmanage.data.local.dao.CategoryDao
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import com.example.restaurantmanage.data.local.entity.CategoryEntity
import com.example.restaurantmanage.data.models.MenuCategory
import com.example.restaurantmanage.data.models.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MenuViewModel(
    private val menuItemDao: MenuItemDao,
    private val categoryDao: CategoryDao
) : ViewModel() {
    private val _categories = MutableStateFlow<List<MenuCategory>>(emptyList())
    val categories: StateFlow<List<MenuCategory>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<MenuCategory?>(null)
    val selectedCategory: StateFlow<MenuCategory?> = _selectedCategory.asStateFlow()

    private val _topSellingItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val topSellingItems: StateFlow<List<MenuItem>> = _topSellingItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadMenuData()
        loadTopSellingItems()
    }

    fun loadMenuData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                combine(
                    categoryDao.getAllCategories().distinctUntilChanged(),
                    menuItemDao.getAllMenuItems().distinctUntilChanged()
                ) { categories, menuItems ->
                    categories.map { category ->
                        MenuCategory(
                            id = category.id,
                            name = category.name,
                            items = menuItems
                                .filter { it.categoryId == category.id }
                                .map { it.toMenuItem() }
                        )
                    }
                }.collect { categories ->
                    _categories.value = categories
                    if (_selectedCategory.value == null && categories.isNotEmpty()) {
                        _selectedCategory.value = categories.first()
                    }
                }
            } catch (e: Exception) {
                _error.value = "Không thể tải dữ liệu menu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadTopSellingItems() {
        viewModelScope.launch {
            try {
                menuItemDao.getTopSellingItems(5)
                    .map { items -> items.map { it.toMenuItem() } }
                    .collect { items ->
                        _topSellingItems.value = items
                    }
            } catch (e: Exception) {
                _error.value = "Không thể tải danh sách món bán chạy: ${e.message}"
            }
        }
    }

    fun selectCategory(category: MenuCategory) {
        _selectedCategory.value = category
    }

    fun addMenuItem(name: String, price: Double, categoryId: String, image: String = "", description: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                val newId = java.util.UUID.randomUUID().toString()
                val targetCategoryId = categoryId.toIntOrNull() ?: run {
                    _error.value = "ID danh mục không hợp lệ"
                    return@launch
                }

                val newItem = MenuItemEntity(
                    id = newId,
                    name = name,
                    price = price,
                    categoryId = targetCategoryId,
                    orderCount = 0,
                    inStock = true,
                    image = image,
                    description = description
                )

                menuItemDao.insertMenuItem(newItem)
                loadMenuData()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Không thể thêm món: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMenuItem(id: String, name: String, price: Double, image: String? = null, description: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                menuItemDao.updateMenuItem(id, name, price)
                if (image != null || description != null) {
                    menuItemDao.updateItemDetails(id, image, description)
                }
                loadMenuData()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Không thể cập nhật món: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMenuItem(menuItem: MenuItemEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                menuItemDao.deleteMenuItem(menuItem)
                loadMenuData()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Không thể xóa món: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateItemStock(itemId: String, inStock: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                menuItemDao.updateItemStock(itemId, inStock)
                loadMenuData()
            } catch (e: Exception) {
                _error.value = "Không thể cập nhật trạng thái món: ${e.message}"
            }
        }
    }

    fun getMenuItemById(id: String): Flow<MenuItemEntity?> {
        return menuItemDao.getMenuItemById(id)
            .catch { e -> 
                _error.value = "Không thể tải thông tin món: ${e.message}"
                emit(null)
            }
            .flowOn(Dispatchers.IO)
    }

    fun clearError() {
        _error.value = null
    }

    private fun MenuItemEntity.toMenuItem(): MenuItem {
        return MenuItem(
            id = id,
            name = name,
            price = price,
            categoryId = categoryId,
            orderCount = orderCount,
            inStock = inStock
        )
    }
}

package com.example.restaurantmanage.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.models.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _featuredItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val featuredItems: StateFlow<List<MenuItem>> = _featuredItems.asStateFlow()

    private val _categories = MutableStateFlow<List<MenuCategory>>(emptyList())
    val categories: StateFlow<List<MenuCategory>> = _categories.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val mockFeaturedItems = listOf(
                MenuItem("1", "Nước ép lê", 50000.0, "Nước uống", imageResId = R.drawable.nuoceple),
                MenuItem("2", "Nước ép dâu", 55000.0, "Nước uống", imageResId = R.drawable.nuocepdau),
                MenuItem("3", "Nước ép thơm", 45000.0, "Nước uống", imageResId = R.drawable.nuocepthom),
                MenuItem("4", "Nước ép táo", 48000.0, "Nước uống", imageResId = R.drawable.nuoceptao)
            )
            _featuredItems.value = mockFeaturedItems

            val mockCategories = listOf(
                MenuCategory("Món chính", R.drawable.food1),
                MenuCategory("Món phụ", R.drawable.food2),
                MenuCategory("Đồ uống", R.drawable.drink)
            )
            _categories.value = mockCategories
        }
    }

    fun getFeaturedItems(): List<MenuItem> {
        return _featuredItems.value
    }

    fun getCategories(): List<MenuCategory> {
        return _categories.value
    }
}

data class MenuCategory(
    val name: String,
    val imageResId: Int
)

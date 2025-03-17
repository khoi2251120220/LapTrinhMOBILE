package com.example.restaurantmanage.data.viewmodels

import androidx.lifecycle.ViewModel
import com.example.restaurantmanage.data.models.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _featuredItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val featuredItems: StateFlow<List<MenuItem>> = _featuredItems

    private val _categories = MutableStateFlow<List<MenuItem>>(emptyList())
    val categories: StateFlow<List<MenuItem>> = _categories

    init {
        _featuredItems.value = listOf(
            MenuItem("1", "Nước ép lê", 50000.0, "pear_juice.jpg", "Nước uống"),
            MenuItem("2", "Nước ép dâu", 55000.0, "strawberry_juice.jpg", "Nước uống"),
            MenuItem("3", "Nước ép thơm", 45000.0, "pineapple_juice.jpg", "Nước uống"),
            MenuItem("4", "Nước ép táo", 48000.0, "apple_juice.jpg", "Nước uống")
        )

        _categories.value = listOf(
            MenuItem("5", "Tên món ăn", 150000.0, "food1.jpg", "Món chính"),
            MenuItem("6", "Tên món ăn", 100000.0, "food2.jpg", "Món phụ")
        )
    }
}
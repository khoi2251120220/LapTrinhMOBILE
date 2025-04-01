package com.example.restaurantmanage.data.viewmodels

import androidx.lifecycle.ViewModel
import com.example.restaurantmanage.data.models.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _featuredItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val featuredItems: StateFlow<List<MenuItem>> = _featuredItems.asStateFlow()

    private val _categories = MutableStateFlow<List<MenuItem>>(emptyList())
    val categories: StateFlow<List<MenuItem>> = _categories.asStateFlow()

    init {
        _featuredItems.value = listOf(
            MenuItem(
                id = "1",
                name = "Nước ép lê",
                price = 50000.0,
                category = "Nước uống",
                image = "pear_juice.jpg"
            ),
            MenuItem(
                id = "2",
                name = "Nước ép dâu",
                price = 55000.0,
                category = "Nước uống",
                image = "strawberry_juice.jpg"
            ),
            MenuItem(
                id = "3",
                name = "Nước ép thơm",
                price = 45000.0,
                category = "Nước uống",
                image = "pineapple_juice.jpg"
            ),
            MenuItem(
                id = "4",
                name = "Nước ép táo",
                price = 48000.0,
                category = "Nước uống",
                image = "apple_juice.jpg"
            )
        )

        _categories.value = listOf(
            MenuItem(
                id = "5",
                name = "Tên món ăn",
                price = 150000.0,
                category = "Món chính",
                image = "food1.jpg"
            ),
            MenuItem(
                id = "6",
                name = "Tên món ăn",
                price = 100000.0,
                category = "Món phụ",
                image = "food2.jpg"
            )
        )
    }
}
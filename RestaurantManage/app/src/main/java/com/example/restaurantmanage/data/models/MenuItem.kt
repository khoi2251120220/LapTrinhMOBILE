package com.example.restaurantmanage.data.models

data class MenuItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val orderCount: Int = 0,
    val inStock: Boolean = true,
    val image: String = "",
    val description: String = ""
)

data class MenuCategory(
    val id: Int,
    val name: String,
    val items: List<MenuItem>
)
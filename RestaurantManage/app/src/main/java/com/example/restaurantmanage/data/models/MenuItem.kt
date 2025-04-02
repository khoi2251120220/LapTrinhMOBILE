package com.example.restaurantmanage.data.models

data class MenuItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val orderCount: Int = 0,
    val inStock: Boolean = true,
    val image: String = "",
    val description: String = "",
    val imageResId: Int = 0,
)

data class MenuCategory(
    val id: Int,
    val name: String,
    val items: List<MenuItem>
)

val sampleItems = listOf(
    MenuItem(id = "1", name = "Tôm sốt cà chua", price = 60000.0, category = "Food", image = ""),
    MenuItem(id = "2", name = "Nước ép trái cây", price = 40000.0, category = "Drink", image = "")
)

val sampleCategories = listOf(
    MenuCategory(id = 1, name = "Thức ăn", items = sampleItems.filter { it.category == "Food" }),
    MenuCategory(id = 2, name = "Đồ uống", items = sampleItems.filter { it.category == "Drink" })
)
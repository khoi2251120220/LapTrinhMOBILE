package com.example.restaurantmanage.data.models

data class MenuItem(
    val id: String,
    val name: String,
    val price: Double,
    val categoryId: Int,
    val orderCount: Int = 0,
    val inStock: Boolean = true,
    val image: String = "",
    val description: String = "",
    val imageResId: Int = 0
)

data class MenuCategory(
    val id: Int,
    val name: String,
    val items: List<MenuItem> = emptyList()
)

// Dữ liệu mẫu
val sampleItems = listOf(
    MenuItem(id = "1", name = "Tôm sốt cà chua", price = 60000.0, categoryId = 1, image = "tom_sot_ca"),
    MenuItem(id = "2", name = "Nước ép trái cây", price = 40000.0, categoryId = 2, image = "nuoc_ep_trai_cay")
)

val sampleCategories = listOf(
    MenuCategory(id = 1, name = "Thức ăn", items = sampleItems.filter { it.categoryId == 1 }),
    MenuCategory(id = 2, name = "Đồ uống", items = sampleItems.filter { it.categoryId == 2 })
)
package com.example.restaurantmanage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.restaurantmanage.data.models.MenuItem

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String = "",
    val items: List<MenuItemEntity> = emptyList()
)

val categories = listOf(
    CategoryEntity(id = 1, name = "Đồ ăn"),
    CategoryEntity(id = 2, name = "Thức uống")
)
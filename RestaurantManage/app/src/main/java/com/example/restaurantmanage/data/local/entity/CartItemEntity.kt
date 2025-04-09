package com.example.restaurantmanage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val cartItemId: Long = 0,
    val menuItemId: String,
    val name: String,
    val price: Double,
    val categoryId: Int,
    val image: String,
    val description: String,
    val quantity: Int
)
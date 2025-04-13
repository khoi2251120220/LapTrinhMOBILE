package com.example.restaurantmanage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = MenuItemEntity::class,
            parentColumns = ["id"], // Assuming MenuItemEntity has an 'id' field
            childColumns = ["menuItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val cartItemId: Long = 0,
    val menuItemId: String,
    val name: String,
    val price: Double,
    val categoryId: Int,
    val image: String,
    val description: String? = null, // Make optional if not always provided
    val quantity: Int = 1 // Default to 1
)
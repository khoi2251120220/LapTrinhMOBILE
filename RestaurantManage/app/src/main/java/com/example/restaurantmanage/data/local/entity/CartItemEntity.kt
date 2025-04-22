package com.example.restaurantmanage.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "menuItemId") val menuItemId: String,
    @ColumnInfo(name = "userId") val userId: String?,
    val quantity: Int,
    val price: Double,
    val notes: String? = null
)
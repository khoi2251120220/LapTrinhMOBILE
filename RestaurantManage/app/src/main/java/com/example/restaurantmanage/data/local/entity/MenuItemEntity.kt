package com.example.restaurantmanage.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "menu_items",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class MenuItemEntity(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    @ColumnInfo(name = "category_id", index = true) val categoryId: Int, // Foreign key
    @ColumnInfo(name = "order_count", defaultValue = "0") val orderCount: Int,
    @ColumnInfo(name = "in_stock", defaultValue = "1") val inStock: Boolean,
    val image: String,
    val description: String
) 
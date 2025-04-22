package com.example.restaurantmanage.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MenuItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["menu_item_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    primaryKeys = ["orderId", "menu_item_id"]
)
data class OrderItemEntity(
    val orderId: String,
    @ColumnInfo(name = "menu_item_id") val menuItemId: String,
    val quantity: Int,
    val price: Double,
    val notes: String = ""
) 
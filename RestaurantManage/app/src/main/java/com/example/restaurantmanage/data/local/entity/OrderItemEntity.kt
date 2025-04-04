package com.example.restaurantmanage.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "order_items",
    primaryKeys = ["order_id", "menu_item_id"],
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["order_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MenuItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["menu_item_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderItemEntity(
    @ColumnInfo(name = "order_id", index = true) val orderId: Int,
    @ColumnInfo(name = "menu_item_id", index = true) val menuItemId: String,
    val quantity: Int
) 
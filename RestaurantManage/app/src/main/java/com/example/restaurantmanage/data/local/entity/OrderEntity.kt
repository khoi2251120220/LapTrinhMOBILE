package com.example.restaurantmanage.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [ForeignKey(
        entity = TableEntity::class,
        parentColumns = ["id"],
        childColumns = ["table_id"],
        onDelete = ForeignKey.SET_NULL // Or CASCADE
    )]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "table_id", index = true) val tableId: Int?,
    @ColumnInfo(name = "start_time") val startTime: Long, // Store Date as Long
    // Order items will be handled in a separate relation table
    @ColumnInfo(name = "total_amount") val totalAmount: Double,
    @ColumnInfo(name = "customer_name") val customerName: String = "Khách hàng",
    val status: String
) 
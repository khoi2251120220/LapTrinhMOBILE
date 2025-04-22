package com.example.restaurantmanage.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "orders",
    foreignKeys = [ForeignKey(
        entity = TableEntity::class,
        parentColumns = ["id"],
        childColumns = ["table_id"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class OrderEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "table_id", index = true) val tableId: Int?,
    @ColumnInfo(name = "user_id", index = true) val userId: String?,
    @ColumnInfo(name = "customer_name") val customerName: String,
    @ColumnInfo(name = "customer_email") val customerEmail: String,
    @ColumnInfo(name = "customer_phone") val customerPhone: String,
    @ColumnInfo(name = "order_date") val orderDate: Date,
    @ColumnInfo(name = "total_amount") val totalAmount: Double,
    val status: String, // PENDING, COMPLETED, CANCELLED
    @ColumnInfo(name = "payment_method") val paymentMethod: String
) 
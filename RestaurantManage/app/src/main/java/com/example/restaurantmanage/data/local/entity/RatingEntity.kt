package com.example.restaurantmanage.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ratings")
data class RatingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "customer_name") val customerName: String,
    val rating: Int,
    val feedback: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = Date().time
) 
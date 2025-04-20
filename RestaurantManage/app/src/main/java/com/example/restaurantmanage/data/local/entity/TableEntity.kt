package com.example.restaurantmanage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tables")
data class TableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val capacity: Int,
    val status: String = "AVAILABLE", // AVAILABLE, RESERVED, OCCUPIED
    val image: String = "" // Đường dẫn đến hình ảnh của bàn
) 
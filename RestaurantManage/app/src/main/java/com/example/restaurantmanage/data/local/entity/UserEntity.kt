package com.example.restaurantmanage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String = "user"
) 
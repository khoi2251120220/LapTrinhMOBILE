package com.example.restaurantmanage.data.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // ADMIN, CUSTOMER
    val status: String, // ACTIVE, INACTIVE
    val createdAt: String
) 
package com.example.restaurantmanage.data.models

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val favoriteItems: List<String> = emptyList(),
    val role: String = "user"
)
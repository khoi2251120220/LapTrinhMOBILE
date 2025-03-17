package com.example.restaurantmanage.data.models

data class MenuItem (
    val id: String,
    val name: String,
    val price: Double,
    val image: String,
    val category: String,
    val description: String = ""
)
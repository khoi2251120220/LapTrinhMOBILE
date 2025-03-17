package com.example.restaurantmanage.data.models

data class CartItem (
    val menuItem: MenuItem,
    var quantity: Int = 1
)
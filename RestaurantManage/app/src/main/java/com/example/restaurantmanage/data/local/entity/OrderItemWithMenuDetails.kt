package com.example.restaurantmanage.data.local.entity

/**
 * Data class representing an order item with additional menu item details
 * Used for displaying order items with their names and other information
 */
data class OrderItemWithMenuDetails(
    val orderId: String,
    val menuItemId: String,
    val menuItemName: String,
    val quantity: Int,
    val price: Double,
    val notes: String,
    val image: String? = null,
    val description: String? = null,
    val categoryId: Int? = null,
    val inStock: Boolean? = true,
    val orderCount: Int? = 0
) 
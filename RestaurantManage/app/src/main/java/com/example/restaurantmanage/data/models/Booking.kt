package com.example.restaurantmanage.data.models

import java.util.Date

data class BookingData(
    val id: Int = 0,
    val imageResId: Int,
    val locationName: String,
    val rating: Float,
    val reviewCount: Int,
    val price: String,
    val customerName: String? = null,
    val phoneNumber: String? = null,
    val numberOfGuests: Int? = null,
    val time: Date? = null,
    val note: String? = null
)
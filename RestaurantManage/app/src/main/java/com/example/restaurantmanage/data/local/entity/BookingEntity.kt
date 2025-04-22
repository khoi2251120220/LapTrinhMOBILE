package com.example.restaurantmanage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey
    val id: String,
    val tableId: Int,
    val userId: String,
    val customerName: String,
    val phoneNumber: String,
    val numberOfGuests: Int,
    val bookingTime: Date,
    val note: String = "",
    val status: String = "PENDING" // PENDING, CONFIRMED, CANCELLED, COMPLETED
)
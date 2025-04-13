package com.example.restaurantmanage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tableName: String,
    val customerName: String,
    val phoneNumber: String,
    val numberOfGuests: Int,
    val time: Date,
    val note: String
)
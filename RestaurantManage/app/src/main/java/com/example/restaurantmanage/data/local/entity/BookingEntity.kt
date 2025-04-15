package com.example.restaurantmanage.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = TableEntity::class,
            parentColumns = ["id"],
            childColumns = ["tableId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tableId: Int,
    val customerName: String,
    val phoneNumber: String,
    val numberOfGuests: Int,
    val time: Date,
    val note: String,
    val status: String = "PENDING" // PENDING, CONFIRMED, CANCELLED, COMPLETED
)
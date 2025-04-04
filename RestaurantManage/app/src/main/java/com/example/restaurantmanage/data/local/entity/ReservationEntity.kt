package com.example.restaurantmanage.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservations",
    foreignKeys = [ForeignKey(
        entity = TableEntity::class,
        parentColumns = ["id"],
        childColumns = ["table_id"],
        onDelete = ForeignKey.SET_NULL // Or CASCADE, depending on requirements
    )]
)
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "table_id", index = true) val tableId: Int?,
    @ColumnInfo(name = "customer_name") val customerName: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "number_of_guests") val numberOfGuests: Int,
    val time: Long, // Store Date as Long (timestamp)
    val note: String
) 
package com.example.restaurantmanage.data.models

import androidx.room.Embedded
import androidx.room.Relation
import com.example.restaurantmanage.data.local.entity.BookingEntity
import com.example.restaurantmanage.data.local.entity.TableEntity

data class TableWithBooking(
    @Embedded val table: TableEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "tableId"
    )
    val bookings: List<BookingEntity>
) 
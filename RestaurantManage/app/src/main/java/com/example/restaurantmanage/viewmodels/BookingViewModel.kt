package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.dao.BookingDao
import com.example.restaurantmanage.data.local.entity.BookingEntity
import com.example.restaurantmanage.data.models.BookingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class BookingViewModel(private val bookingDao: BookingDao) : ViewModel() {

    val data: StateFlow<List<BookingData>> = bookingDao.getAllBookings().map { entities ->
        entities.map { entity ->
            BookingData(
                id = entity.id,
                imageResId = 0,
                locationName = entity.tableName,
                rating = 4.5f,
                reviewCount = 100,
                price = "500,000 VND",
                customerName = entity.customerName,
                phoneNumber = entity.phoneNumber,
                numberOfGuests = entity.numberOfGuests,
                time = entity.time,
                note = entity.note
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createBooking(
        tableName: String,
        customerName: String,
        phoneNumber: String,
        numberOfGuests: Int,
        time: Date,
        note: String
    ) {
        viewModelScope.launch {
            val bookingEntity = BookingEntity(
                tableName = tableName,
                customerName = customerName,
                phoneNumber = phoneNumber,
                numberOfGuests = numberOfGuests,
                time = time,
                note = note
            )
            bookingDao.insertBooking(bookingEntity)
        }
    }
}
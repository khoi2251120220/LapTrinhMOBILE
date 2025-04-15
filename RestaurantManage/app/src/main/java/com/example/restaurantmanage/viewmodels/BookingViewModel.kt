package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.dao.BookingDao
import com.example.restaurantmanage.data.local.dao.TableDao
import com.example.restaurantmanage.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class BookingViewModel(
    private val bookingDao: BookingDao,
    private val tableDao: TableDao
) : ViewModel() {

    val bookings = bookingDao.getAllBookings()
    val availableTables = tableDao.getTablesByStatus("AVAILABLE")

    fun createBooking(
        tableId: Int,
        customerName: String,
        phoneNumber: String,
        numberOfGuests: Int,
        time: Date,
        note: String
    ) {
        viewModelScope.launch {
            val booking = BookingEntity(
                tableId = tableId,
                customerName = customerName,
                phoneNumber = phoneNumber,
                numberOfGuests = numberOfGuests,
                time = time,
                note = note
            )
            bookingDao.insertBooking(booking)
            tableDao.updateTableStatus(tableId, "RESERVED")
        }
    }

    fun updateBookingStatus(bookingId: Int, status: String) {
        viewModelScope.launch {
            bookingDao.updateBookingStatus(bookingId, status)
        }
    }

    fun cancelBooking(booking: BookingEntity) {
        viewModelScope.launch {
            bookingDao.updateBookingStatus(booking.id, "CANCELLED")
            tableDao.updateTableStatus(booking.tableId, "AVAILABLE")
        }
    }

    fun completeBooking(booking: BookingEntity) {
        viewModelScope.launch {
            bookingDao.updateBookingStatus(booking.id, "COMPLETED")
            tableDao.updateTableStatus(booking.tableId, "OCCUPIED")
        }
    }

    class Factory(
        private val bookingDao: BookingDao,
        private val tableDao: TableDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
                return BookingViewModel(bookingDao, tableDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
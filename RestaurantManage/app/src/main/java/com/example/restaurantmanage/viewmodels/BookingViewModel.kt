package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.R
import com.example.restaurantmanage.data.models.Table
import com.example.restaurantmanage.data.models.TableStatus
import com.example.restaurantmanage.data.models.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import com.example.restaurantmanage.data.models.BookingData

class BookingViewModel : ViewModel() {
    private val _data = MutableStateFlow<List<BookingData>>(emptyList())
    val data: StateFlow<List<BookingData>> = _data.asStateFlow()

    private val _tables = MutableStateFlow<List<Table>>(emptyList())
    val tables: StateFlow<List<Table>> = _tables.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Mock data cho tables
            val mockTables = listOf(
                Table(
                    id = 1,
                    name = "Bàn 01",
                    capacity = 4,
                    status = TableStatus.AVAILABLE
                ),
                Table(
                    id = 2,
                    name = "Bàn 02",
                    capacity = 6,
                    status = TableStatus.AVAILABLE
                ),
                Table(
                    id = 3,
                    name = "Bàn VIP 01",
                    capacity = 8,
                    status = TableStatus.AVAILABLE
                ),
                Table(
                    id = 4,
                    name = "Bàn 04",
                    capacity = 4,
                    status = TableStatus.AVAILABLE
                )
            )
            _tables.value = mockTables

            val bookingData = mockTables.map { table ->
                BookingData(
                    locationName = table.name,
                    rating = 4.8f,
                    reviewCount = 500,
                    price = "1,000,000",
                    imageResId = when (table.capacity) {
                        in 1..4 -> R.drawable.ic_booking_1
                        in 5..6 -> R.drawable.ic_booking_2
                        else -> R.drawable.ic_booking_1
                    }
                )
            }
            _data.value = bookingData
        }
    }

    fun createBooking(
        tableName: String,
        customerName: String,
        phoneNumber: String,
        numberOfGuests: Int,
        time: Date,
        note: String = ""
    ) {
        viewModelScope.launch {
            val updatedTables = _tables.value.map { table ->
                if (table.name == tableName) {
                    table.copy(
                        status = TableStatus.RESERVED,
                        reservation = Reservation(
                            id = (_tables.value.size + 1),
                            customerName = customerName,
                            phoneNumber = phoneNumber,
                            numberOfGuests = numberOfGuests,
                            time = time,
                            note = note
                        )
                    )
                } else {
                    table
                }
            }
            _tables.value = updatedTables

            val updatedBookingData = updatedTables.map { table ->
                BookingData(
                    locationName = table.name,
                    rating = 4.8f,
                    reviewCount = 500,
                    price = "1,000,000",
                    imageResId = when (table.capacity) {
                        in 1..4 -> R.drawable.ic_booking_1
                        in 5..6 -> R.drawable.ic_booking_2
                        else -> R.drawable.ic_booking_1
                    }
                )
            }
            _data.value = updatedBookingData
        }
    }

    fun getAvailableTables(): List<Table> {
        return _tables.value.filter { it.status == TableStatus.AVAILABLE }
    }
}


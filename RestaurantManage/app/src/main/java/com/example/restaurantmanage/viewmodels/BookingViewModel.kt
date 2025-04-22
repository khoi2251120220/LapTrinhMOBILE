package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.dao.BookingDao
import com.example.restaurantmanage.data.local.dao.TableDao
import com.example.restaurantmanage.data.local.entity.BookingEntity
import com.example.restaurantmanage.data.local.entity.TableEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class BookingViewModel(
    private val bookingDao: BookingDao,
    private val tableDao: TableDao
) : ViewModel() {
    // All available tables (status = AVAILABLE)
    val availableTables = tableDao.getTablesByStatus("AVAILABLE")
    
    // Filtered tables based on search query
    private val _filteredTables = MutableStateFlow<List<TableEntity>>(emptyList())
    val filteredTables: StateFlow<List<TableEntity>> = _filteredTables
    
    // User's bookings
    private val _userBookings = MutableStateFlow<List<BookingEntity>>(emptyList())
    val userBookings: StateFlow<List<BookingEntity>> = _userBookings
    
    // Current user ID
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    
    init {
        loadUserBookings()
    }
    
    // Set filtered tables
    fun setFilteredTables(tables: List<TableEntity>) {
        _filteredTables.value = tables
    }
    
    // Search tables by name
    fun searchTables(query: String) {
        viewModelScope.launch {
            tableDao.searchTablesByName("%$query%").collect { tables ->
                // Only include tables with AVAILABLE status
                val availableTables = tables.filter { it.status == "AVAILABLE" }
                _filteredTables.value = availableTables
            }
        }
    }

    fun createBooking(
        tableId: Int,
        customerName: String,
        phoneNumber: String,
        numberOfGuests: Int,
        bookingTime: Date,
        note: String
    ) {
        viewModelScope.launch {
            val bookingId = UUID.randomUUID().toString()
            
            val booking = BookingEntity(
                id = bookingId,
                tableId = tableId,
                userId = currentUserId ?: "anonymous",
                customerName = customerName,
                phoneNumber = phoneNumber,
                numberOfGuests = numberOfGuests,
                bookingTime = bookingTime,
                note = note,
                status = "CONFIRMED"
            )
            
            bookingDao.insertBooking(booking)
            
            // Cập nhật trạng thái bàn thành RESERVED ngay lập tức
            tableDao.updateTableStatus(tableId, "RESERVED")
            
            // Reload user bookings
            loadUserBookings()
        }
    }
    
    // Load bookings for current user
    private fun loadUserBookings() {
        viewModelScope.launch {
            currentUserId?.let { userId ->
                bookingDao.getBookingsByUserId(userId).collect { bookings ->
                    _userBookings.value = bookings
                }
            }
        }
    }
    
    // Cancel a booking
    fun cancelBooking(booking: BookingEntity) {
        viewModelScope.launch {
            bookingDao.updateBookingStatus(booking.id, "CANCELLED")
            
            // Update the table status back to AVAILABLE if it was RESERVED
            val table = tableDao.getTableById(booking.tableId)
            if (table?.status == "RESERVED") {
                tableDao.updateTableStatus(booking.tableId, "AVAILABLE")
            }
            
            // Reload user bookings
            loadUserBookings()
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
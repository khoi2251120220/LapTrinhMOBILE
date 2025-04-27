package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.dao.TableDao
import com.example.restaurantmanage.data.local.dao.BookingDao
import com.example.restaurantmanage.data.local.entity.TableEntity
import com.example.restaurantmanage.data.local.entity.BookingEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class TableManagementViewModel(
    private val tableDao: TableDao,
    private val bookingDao: BookingDao
) : ViewModel() {

    val tables = tableDao.getAllTables()
    val availableTables = tableDao.getTablesByStatus("AVAILABLE")
    val reservedTables = tableDao.getTablesByStatus("RESERVED")
    val occupiedTables = tableDao.getTablesByStatus("OCCUPIED")
    
    // Order count for each table - this will be used for analytics
    private val _tableOrderCount = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val tableOrderCount: StateFlow<Map<Int, Int>> = _tableOrderCount
    
    // Selected table booking
    private val _selectedTableBooking = MutableStateFlow<BookingEntity?>(null)
    val selectedTableBooking: StateFlow<BookingEntity?> = _selectedTableBooking
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // Firebase Firestore instance
    private val firestore = FirebaseFirestore.getInstance()

    // Thêm các state mới để lưu trữ dữ liệu cho giao diện
    // Danh sách đặt bàn theo ngày
    private val _bookingsForDate = MutableStateFlow<List<BookingEntity>>(emptyList())
    val bookingsForDate: StateFlow<List<BookingEntity>> = _bookingsForDate
    
    // Booking được chọn để xem chi tiết
    private val _selectedBooking = MutableStateFlow<BookingEntity?>(null)
    val selectedBooking: StateFlow<BookingEntity?> = _selectedBooking
    
    // Thống kê bàn
    private val _tableStats = MutableStateFlow(TableStats())
    val tableStats: StateFlow<TableStats> = _tableStats

    init {
        // Load initial table order counts
        updateTableOrderCounts()
        // Sync data from Firestore
        viewModelScope.launch {
            syncBookingsFromFirestore()
        }
    }

    fun getFilteredTables(tabIndex: Int): Flow<List<TableEntity>> {
        return when (tabIndex) {
            0 -> tables
            1 -> availableTables
            2 -> reservedTables
            3 -> occupiedTables
            else -> tables
        }
    }
    
    // Refresh all data
    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            syncBookingsFromFirestore()
            _isLoading.value = false
        }
    }
    
    // Sync bookings from Firestore to local database
    private suspend fun syncBookingsFromFirestore() {
        withContext(Dispatchers.IO) {
            try {
                _isLoading.value = true
                
                // Get all active bookings from Firestore
                val firestoreBookings = firestore.collection("bookings")
                    .whereIn("status", listOf("CONFIRMED", "PENDING"))
                    .get()
                    .await()
                
                // Process each booking
                for (document in firestoreBookings.documents) {
                    val bookingId = document.id
                    val tableId = document.getLong("tableId")?.toInt() ?: continue
                    val userId = document.getString("userId") ?: "anonymous"
                    val customerName = document.getString("customerName") ?: "Unknown"
                    val phoneNumber = document.getString("phoneNumber") ?: ""
                    val numberOfGuests = document.getLong("numberOfGuests")?.toInt() ?: 1
                    val bookingTime = document.getDate("bookingTime") ?: Date()
                    val note = document.getString("note") ?: ""
                    val status = document.getString("status") ?: "CONFIRMED"
                    
                    // Check if booking is for today or future
                    val today = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time
                    
                    if (bookingTime.after(today) || isSameDay(bookingTime, today)) {
                        // Check if booking exists in local database
                        val localBooking = bookingDao.getBookingById(bookingId)
                        
                        if (localBooking == null) {
                            // Insert new booking
                            val booking = BookingEntity(
                                id = bookingId,
                                tableId = tableId,
                                userId = userId,
                                customerName = customerName,
                                phoneNumber = phoneNumber,
                                numberOfGuests = numberOfGuests,
                                bookingTime = bookingTime,
                                note = note,
                                status = status
                            )
                            bookingDao.insertBooking(booking)
                            
                            // Update table status for bookings that are happening soon
                            val now = Calendar.getInstance().time
                            val bookingCal = Calendar.getInstance().apply { time = bookingTime }
                            val nowCal = Calendar.getInstance().apply { time = now }
                            
                            if (isSameDay(bookingTime, now) && 
                                (bookingCal.get(Calendar.HOUR_OF_DAY) - nowCal.get(Calendar.HOUR_OF_DAY) <= 2)) {
                                tableDao.updateTableStatus(tableId, "RESERVED")
                            }
                        } else if (localBooking.status != status) {
                            // Update booking status if changed
                            bookingDao.updateBookingStatus(bookingId, status)
                            
                            // Update table status if booking was canceled
                            if (status == "CANCELLED") {
                                val table = tableDao.getTableById(tableId)
                                if (table?.status == "RESERVED") {
                                    // Check if there are any other active bookings for this table
                                    val hasOtherBookings = bookingDao.getActiveBookingForTable(tableId) != null
                                    if (!hasOtherBookings) {
                                        tableDao.updateTableStatus(tableId, "AVAILABLE")
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Update table statuses based on bookings
                updateTableStatusesBasedOnBookings()
                
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                // Handle error
                e.printStackTrace()
            }
        }
    }
    
    // Helper function to check if two dates are on the same day
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    
    // Update table statuses based on active bookings
    private suspend fun updateTableStatusesBasedOnBookings() {
        withContext(Dispatchers.IO) {
            val allTables = tableDao.getAllTablesAsList()
            val now = Calendar.getInstance().time
            
            for (table in allTables) {
                // Skip tables that are currently occupied
                if (table.status == "OCCUPIED") continue
                
                // Check for active bookings for this table
                val activeBooking = bookingDao.getActiveBookingForTable(table.id)
                
                if (activeBooking != null) {
                    // Calculate the booking time window
                    val bookingTime = activeBooking.bookingTime
                    val bookingCal = Calendar.getInstance().apply { time = bookingTime }
                    val nowCal = Calendar.getInstance().apply { time = now }
                    
                    // If the booking is today and within 2 hours or in the past (but today)
                    if (isSameDay(bookingTime, now)) {
                        val hourDiff = bookingCal.get(Calendar.HOUR_OF_DAY) - nowCal.get(Calendar.HOUR_OF_DAY)
                        
                        if (hourDiff <= 2 && hourDiff >= -2) {
                            tableDao.updateTableStatus(table.id, "RESERVED")
                        }
                    }
                } else if (table.status == "RESERVED") {
                    // No active bookings, but table is marked as reserved
                    // Check if there are any upcoming bookings in Firestore
                    try {
                        val firestoreBookings = firestore.collection("bookings")
                            .whereEqualTo("tableId", table.id)
                            .whereIn("status", listOf("CONFIRMED", "PENDING"))
                            .get()
                            .await()
                        
                        if (firestoreBookings.isEmpty) {
                            // No active bookings in Firestore either, set to available
                            tableDao.updateTableStatus(table.id, "AVAILABLE")
                        }
                    } catch (e: Exception) {
                        // On error, leave as is
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    
    // Load booking for a specific table
    fun loadBookingForTable(tableId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Try to get booking from local database
                var booking = bookingDao.getActiveBookingForTable(tableId)
                
                // If not found in local database, try to get from Firestore
                if (booking == null) {
                    val firestoreBookings = firestore.collection("bookings")
                        .whereEqualTo("tableId", tableId)
                        .whereIn("status", listOf("CONFIRMED", "PENDING"))
                        .get()
                        .await()
                    
                    if (!firestoreBookings.isEmpty) {
                        // Get the most recent booking
                        val document = firestoreBookings.documents
                            .mapNotNull { doc -> doc.getDate("bookingTime")?.let { Pair(doc, it) } }
                            .sortedBy { it.second }
                            .firstOrNull()?.first
                        
                        if (document != null) {
                            val bookingId = document.id
                            val userId = document.getString("userId") ?: "anonymous"
                            val customerName = document.getString("customerName") ?: "Unknown"
                            val phoneNumber = document.getString("phoneNumber") ?: ""
                            val numberOfGuests = document.getLong("numberOfGuests")?.toInt() ?: 1
                            val bookingTime = document.getDate("bookingTime") ?: Date()
                            val note = document.getString("note") ?: ""
                            val status = document.getString("status") ?: "CONFIRMED"
                            
                            booking = BookingEntity(
                                id = bookingId,
                                tableId = tableId,
                                userId = userId,
                                customerName = customerName,
                                phoneNumber = phoneNumber,
                                numberOfGuests = numberOfGuests,
                                bookingTime = bookingTime,
                                note = note,
                                status = status
                            )
                            
                            // Save to local database
                            bookingDao.insertBooking(booking)
                        }
                    }
                }
                
                // Switch to main thread to update UI state
                withContext(Dispatchers.Main) {
                    _selectedTableBooking.value = booking
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // Hủy đặt bàn
    fun cancelBooking(booking: BookingEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Cập nhật trạng thái đặt bàn thành CANCELLED trong Room Database
                bookingDao.updateBookingStatus(booking.id, "CANCELLED")
                
                // Cập nhật trạng thái trong Firestore
                firestore.collection("bookings").document(booking.id)
                    .update("status", "CANCELLED")
                    .await()
                
                // Check if there are other active bookings for this table
                val otherBookings = bookingDao.getActiveBookingForTable(booking.tableId)
                
                // If no other bookings, set table to AVAILABLE
                if (otherBookings == null) {
                    tableDao.updateTableStatus(booking.tableId, "AVAILABLE")
                }
                
                // Reset selected booking (on main thread)
                withContext(Dispatchers.Main) {
                    _selectedTableBooking.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addTable(name: String, capacity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val table = TableEntity(
                name = name,
                capacity = capacity
            )
            tableDao.insertTable(table)
        }
    }
    
    fun addTableWithImage(name: String, capacity: Int, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val table = TableEntity(
                name = name,
                capacity = capacity,
                image = imagePath
            )
            tableDao.insertTable(table)
        }
    }

    fun updateTableStatus(tableId: Int, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            tableDao.updateTableStatus(tableId, status)
        }
    }
    
    fun incrementTableOrderCount(tableId: Int) {
        viewModelScope.launch {
            // Get the current count
            val currentCount = _tableOrderCount.value[tableId] ?: 0
            // Create a new map with the updated count
            val updatedMap = _tableOrderCount.value.toMutableMap().apply {
                put(tableId, currentCount + 1)
            }
            _tableOrderCount.value = updatedMap
        }
    }
    
    private fun updateTableOrderCounts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // This would typically come from a repository or database
                // For now, we'll initialize with zeroes for all tables
                val tableList = tableDao.getAllTablesAsList()
                val countMap = tableList.associate { it.id to 0 }
                withContext(Dispatchers.Main) {
                    _tableOrderCount.value = countMap
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTable(table: TableEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            tableDao.deleteTable(table)
        }
    }

    fun checkTableAvailability(tableId: Int, bookingTime: Date): Flow<Boolean> {
        val calendar = Calendar.getInstance()
        calendar.time = bookingTime
        
        // Kiểm tra trong khoảng 2 giờ
        val startTime = calendar.apply { add(Calendar.HOUR, -1) }.time
        val endTime = calendar.apply { add(Calendar.HOUR, 2) }.time

        return flow {
            withContext(Dispatchers.IO) {
                val isBooked = bookingDao.isTableBooked(tableId, startTime, endTime)
                emit(isBooked)
            }
        }
    }
    
    fun searchTablesByName(query: String): Flow<List<TableEntity>> {
        return tableDao.searchTablesByName("%$query%")
    }

    // Thêm function để tải danh sách đặt bàn theo ngày
    fun loadBookingsForDate(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.time
                
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                val endOfDay = calendar.time
                
                // Lấy danh sách đặt bàn từ Room Database
                val localBookings = bookingDao.getBookingsBetweenDates(startOfDay, endOfDay)
                
                // Lấy danh sách đặt bàn từ Firestore
                try {
                    val firestoreBookings = firestore.collection("bookings")
                        .whereGreaterThanOrEqualTo("bookingTime", startOfDay)
                        .whereLessThan("bookingTime", endOfDay)
                        .whereIn("status", listOf("CONFIRMED", "PENDING"))
                        .get()
                        .await()
                    
                    val bookingsFromFirestore = mutableListOf<BookingEntity>()
                    
                    for (document in firestoreBookings.documents) {
                        val bookingId = document.id
                        val tableId = document.getLong("tableId")?.toInt() ?: continue
                        val userId = document.getString("userId") ?: "anonymous"
                        val customerName = document.getString("customerName") ?: "Unknown"
                        val phoneNumber = document.getString("phoneNumber") ?: ""
                        val numberOfGuests = document.getLong("numberOfGuests")?.toInt() ?: 1
                        val bookingTime = document.getDate("bookingTime") ?: Date()
                        val note = document.getString("note") ?: ""
                        val status = document.getString("status") ?: "CONFIRMED"
                        
                        // Kiểm tra xem booking này đã có trong danh sách local chưa
                        if (localBookings.none { it.id == bookingId }) {
                            val booking = BookingEntity(
                                id = bookingId,
                                tableId = tableId,
                                userId = userId,
                                customerName = customerName,
                                phoneNumber = phoneNumber,
                                numberOfGuests = numberOfGuests,
                                bookingTime = bookingTime,
                                note = note,
                                status = status
                            )
                            
                            // Thêm vào danh sách
                            bookingsFromFirestore.add(booking)
                            
                            // Lưu vào Room Database
                            bookingDao.insertBooking(booking)
                        }
                    }
                    
                    // Kết hợp danh sách từ local và Firestore
                    val allBookings = (localBookings + bookingsFromFirestore)
                        .distinctBy { it.id }
                        .filter { it.status == "CONFIRMED" || it.status == "PENDING" }
                        .sortedBy { it.bookingTime }
                    
                    withContext(Dispatchers.Main) {
                        _bookingsForDate.value = allBookings
                    }
                    
                } catch (e: Exception) {
                    // Nếu có lỗi, chỉ lấy từ Room Database
                    withContext(Dispatchers.Main) {
                        _bookingsForDate.value = localBookings
                            .filter { it.status == "CONFIRMED" || it.status == "PENDING" }
                            .sortedBy { it.bookingTime }
                    }
                    e.printStackTrace()
                }
                
                // Cập nhật thống kê
                updateTableStats()
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // Chọn booking để xem chi tiết
    fun selectBooking(booking: BookingEntity) {
        _selectedBooking.value = booking
    }
    
    // Cập nhật thống kê
    private fun updateTableStats() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val allTablesCount = tableDao.getAllTablesAsList().size
                val availableTablesCount = tableDao.getTablesByStatusAsList("AVAILABLE").size
                val reservedTablesCount = tableDao.getTablesByStatusAsList("RESERVED").size
                val occupiedTablesCount = tableDao.getTablesByStatusAsList("OCCUPIED").size
                
                // Lấy số lượng đặt bàn cho ngày hôm nay
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                
                val tomorrow = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                
                val dayAfterTomorrow = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_MONTH, 2)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                
                val bookingsToday = bookingDao.getBookingsBetweenDates(today, tomorrow)
                    .filter { it.status == "CONFIRMED" || it.status == "PENDING" }
                    .size
                
                val bookingsTomorrow = bookingDao.getBookingsBetweenDates(tomorrow, dayAfterTomorrow)
                    .filter { it.status == "CONFIRMED" || it.status == "PENDING" }
                    .size
                
                withContext(Dispatchers.Main) {
                    _tableStats.value = TableStats(
                        totalTables = allTablesCount,
                        availableTables = availableTablesCount,
                        reservedTables = reservedTablesCount,
                        occupiedTables = occupiedTablesCount,
                        bookingsToday = bookingsToday,
                        bookingsTomorrow = bookingsTomorrow
                    )
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Thêm data class cho thống kê bàn
    data class TableStats(
        val totalTables: Int = 0,
        val availableTables: Int = 0,
        val reservedTables: Int = 0,
        val occupiedTables: Int = 0,
        val bookingsToday: Int = 0,
        val bookingsTomorrow: Int = 0
    )

    class Factory(
        private val tableDao: TableDao,
        private val bookingDao: BookingDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TableManagementViewModel::class.java)) {
                return TableManagementViewModel(tableDao, bookingDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
package com.example.restaurantmanage.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.dao.BookingDao
import com.example.restaurantmanage.data.local.dao.TableDao
import com.example.restaurantmanage.data.local.entity.BookingEntity
import com.example.restaurantmanage.data.local.entity.TableEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlin.math.abs

class BookingViewModel(
    private val bookingDao: BookingDao,
    private val tableDao: TableDao
) : ViewModel() {
    // Tất cả các bàn có sẵn
    val allTables = tableDao.getAllTables()
    
    // Filtered tables based on search query
    private val _filteredTables = MutableStateFlow<List<TableEntity>>(emptyList())
    val filteredTables: StateFlow<List<TableEntity>> = _filteredTables
    
    // User's bookings
    private val _userBookings = MutableStateFlow<List<BookingEntity>>(emptyList())
    val userBookings: StateFlow<List<BookingEntity>> = _userBookings
    
    // Booking slots for a specific table
    private val _tableBookingSlots = MutableStateFlow<Map<Date, Boolean>>(emptyMap())
    val tableBookingSlots: StateFlow<Map<Date, Boolean>> = _tableBookingSlots
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // Current user ID
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    
    // Firebase Firestore reference
    private val firestore = FirebaseFirestore.getInstance()
    
    init {
        loadUserBookings()
        syncTablesFromFirestore()
    }
    
    // Sync tables from Firestore to local database
    private fun syncTablesFromFirestore() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                
                // Get all tables from Firestore
                val firestoreTables = firestore.collection("tables")
                    .get()
                    .await()
                
                if (!firestoreTables.isEmpty) {
                    // Check if local database has tables
                    val localTableCount = tableDao.getTableCount()
                    
                    // If no local tables or fewer tables than Firestore, clear and resync
                    if (localTableCount == 0 || localTableCount < firestoreTables.size()) {
                        // Clear local tables first to avoid duplicates
                        tableDao.deleteAllTables()
                        
                        // Process each table from Firestore
                        for (document in firestoreTables.documents) {
                            val tableId = document.getString("id") ?: continue
                            val tableName = document.getString("name") ?: "Unknown Table"
                            val capacity = document.getLong("capacity")?.toInt() ?: 2
                            val status = document.getString("status") ?: "AVAILABLE"
                            val image = document.getString("image") ?: ""
                            
                            // Insert into local database
                            val localId = if (tableId.toIntOrNull() != null) tableId.toInt() else 0
                            val tableEntity = TableEntity(
                                id = localId,
                                name = tableName,
                                capacity = capacity,
                                status = status,
                                image = image
                            )
                            tableDao.insertTable(tableEntity)
                        }
                        
                        // Update filtered tables after sync
                        val updatedTables = tableDao.getAllTablesAsList()
                        withContext(Dispatchers.Main) {
                            _filteredTables.value = updatedTables
                        }
                    }
                }
                
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                e.printStackTrace()
            }
        }
    }
    
    // Set filtered tables
    fun setFilteredTables(tables: List<TableEntity>) {
        _filteredTables.value = tables
    }
    
    // Search tables by name
    fun searchTables(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                tableDao.searchTablesByName("%$query%").collect { tables ->
                    withContext(Dispatchers.Main) {
                        _filteredTables.value = tables
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // Refresh data from Firestore
    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            syncTablesFromFirestore()
            loadUserBookings()
            _isLoading.value = false
        }
    }
    
    // Kiểm tra và tải các khung giờ đã đặt cho một bàn cụ thể trong ngày
    fun loadTableBookingSlots(tableId: Int, selectedDate: Date) {
        viewModelScope.launch {
            val timeSlots = generateTimeSlots(selectedDate)
            val now = Calendar.getInstance().time
            
            // Kiểm tra tính khả dụng của mỗi khung giờ
            val availabilityMap = mutableMapOf<Date, Boolean>()
            
            for (slot in timeSlots) {
                // Kiểm tra xem thời gian đặt bàn có ở quá khứ không
                val isPastTime = slot.before(now)
                
                if (isPastTime) {
                    // Nếu là thời gian quá khứ, đánh dấu là không có sẵn
                    availabilityMap[slot] = false
                } else {
                    // Nếu không phải thời gian quá khứ, kiểm tra xem bàn có được đặt vào slot này chưa
                    val slotStart = Calendar.getInstance().apply {
                        time = slot
                        add(Calendar.HOUR, -1)
                    }.time
                    
                    val slotEnd = Calendar.getInstance().apply {
                        time = slot
                        add(Calendar.HOUR, 1)
                    }.time
                    
                    // Check in local database first
                    var isBooked = withContext(Dispatchers.IO) {
                        bookingDao.isTableBooked(tableId, slotStart, slotEnd)
                    }
                    
                    // If not booked locally, check Firestore for bookings
                    if (!isBooked) {
                        try {
                            val firestoreBookings = firestore.collection("bookings")
                                .whereEqualTo("tableId", tableId)
                                .whereIn("status", listOf("CONFIRMED", "PENDING"))
                                .get()
                                .await()
                            
                            for (document in firestoreBookings.documents) {
                                val bookingTime = document.getDate("bookingTime")
                                if (bookingTime != null) {
                                    val bookingCal = Calendar.getInstance().apply { time = bookingTime }
                                    val slotCal = Calendar.getInstance().apply { time = slot }
                                    
                                    // Check if the booking time is within +/- 1 hour of the slot
                                    if (abs(bookingCal.timeInMillis - slotCal.timeInMillis) <= 3600000) {
                                        isBooked = true
                                        break
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    
                    availabilityMap[slot] = !isBooked
                }
            }
            
            _tableBookingSlots.value = availabilityMap
        }
    }
    
    // Tạo danh sách các khung giờ đặt bàn từ 8:00 đến 22:00
    private fun generateTimeSlots(date: Date): List<Date> {
        val timeSlots = mutableListOf<Date>()
        val calendar = Calendar.getInstance()
        calendar.time = date
        
        // Đặt thời gian bắt đầu là 8:00 sáng
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        // Tạo các khung giờ từ 8:00 đến 22:00, mỗi khung cách nhau 1 giờ
        for (hour in 8..21) {
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            timeSlots.add(calendar.time)
            
            // Tạo một bản sao của thời gian để tránh tham chiếu đến cùng một đối tượng
            val slot = calendar.time.clone() as Date
            timeSlots.add(slot)
        }
        
        return timeSlots
    }
    
    private fun markTimeSlotAsUnavailable(bookingTime: Date, timeSlots: MutableMap<Date, Boolean>) {
        val bookingCalendar = Calendar.getInstance()
        bookingCalendar.time = bookingTime
        
        // Tìm khung giờ chính xác hoặc gần nhất
        val slot = timeSlots.keys.find { slotTime ->
            val slotCalendar = Calendar.getInstance()
            slotCalendar.time = slotTime
            slotCalendar.get(Calendar.HOUR_OF_DAY) == bookingCalendar.get(Calendar.HOUR_OF_DAY)
        }
        
        // Đánh dấu khung giờ này là không khả dụng
        slot?.let { timeSlots[it] = false }
    }

    fun createBooking(
        tableId: Int,
        customerName: String,
        phoneNumber: String,
        numberOfGuests: Int,
        bookingTime: Date,
        note: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
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
                
                // Lưu vào Room Database
                bookingDao.insertBooking(booking)
                
                // Lưu lên Firestore
                try {
                    val firestoreBooking = hashMapOf(
                        "id" to bookingId,
                        "tableId" to tableId,
                        "userId" to (currentUserId ?: "anonymous"),
                        "customerName" to customerName,
                        "phoneNumber" to phoneNumber,
                        "numberOfGuests" to numberOfGuests,
                        "bookingTime" to bookingTime,
                        "note" to note,
                        "status" to "CONFIRMED",
                        "createdAt" to Date()
                    )
                    
                    firestore.collection("bookings").document(bookingId)
                        .set(firestoreBooking)
                        .await()
                    
                    // Update table status in Firestore
                    updateTableStatusInFirestore(tableId, "RESERVED")
                } catch (e: Exception) {
                    // Xử lý lỗi khi không thể lưu lên Firestore
                    e.printStackTrace()
                }
                
                // Reload user bookings
                loadUserBookings()
                
                // Cập nhật lại khung giờ đặt bàn
                loadTableBookingSlots(tableId, bookingTime)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // Update table status in Firestore
    private suspend fun updateTableStatusInFirestore(tableId: Int, status: String) {
        try {
            // Get the table entity from Room
            val tableEntity = tableDao.getTableById(tableId) ?: return
            
            // Update status locally
            tableDao.updateTableStatus(tableId, status)
            
            // Update in Firestore
            val updates = hashMapOf<String, Any>(
                "status" to status
            )
            
            firestore.collection("tables").document(tableId.toString())
                .update(updates)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // Load bookings for current user
    private fun loadUserBookings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentUserId?.let { userId ->
                    // First try to get from local database
                    val localBookings = bookingDao.getBookingsByUserIdAsList(userId)
                    
                    // Update state with local bookings
                    withContext(Dispatchers.Main) {
                        _userBookings.value = localBookings
                    }
                    
                    // Then try to get from Firestore for the most up-to-date data
                    try {
                        val firestoreBookings = firestore.collection("bookings")
                            .whereEqualTo("userId", userId)
                            .get()
                            .await()
                        
                        if (!firestoreBookings.isEmpty) {
                            val bookingsList = mutableListOf<BookingEntity>()
                            
                            for (document in firestoreBookings.documents) {
                                val bookingId = document.id
                                val tableId = document.getLong("tableId")?.toInt() ?: continue
                                val customerName = document.getString("customerName") ?: "Unknown"
                                val phoneNumber = document.getString("phoneNumber") ?: ""
                                val numberOfGuests = document.getLong("numberOfGuests")?.toInt() ?: 1
                                val bookingTime = document.getDate("bookingTime") ?: Date()
                                val note = document.getString("note") ?: ""
                                val status = document.getString("status") ?: "CONFIRMED"
                                
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
                                
                                bookingsList.add(booking)
                                
                                // Sync to local database if not exists or status changed
                                val localBooking = bookingDao.getBookingById(bookingId)
                                if (localBooking == null) {
                                    bookingDao.insertBooking(booking)
                                } else if (localBooking.status != status) {
                                    bookingDao.updateBookingStatus(bookingId, status)
                                }
                            }
                            
                            // Update state with Firestore bookings
                            withContext(Dispatchers.Main) {
                                _userBookings.value = bookingsList
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // Cancel a booking
    fun cancelBooking(booking: BookingEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Cập nhật trạng thái trong Room Database
                bookingDao.updateBookingStatus(booking.id, "CANCELLED")
                
                // Cập nhật trạng thái trong Firestore
                try {
                    firestore.collection("bookings").document(booking.id)
                        .update("status", "CANCELLED")
                        .await()
                    
                    // Check if there are any other bookings for this table
                    val otherBookings = bookingDao.getActiveBookingCountForTable(booking.tableId)
                    
                    if (otherBookings == 0) {
                        // Update table status back to available in both local and Firestore
                        tableDao.updateTableStatus(booking.tableId, "AVAILABLE")
                        updateTableStatusInFirestore(booking.tableId, "AVAILABLE")
                    }
                } catch (e: Exception) {
                    // Xử lý lỗi khi không thể cập nhật Firestore
                    e.printStackTrace()
                }
                
                // Reload user bookings
                loadUserBookings()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // Kiểm tra xem một khung giờ cụ thể có khả dụng không
    fun isTimeSlotAvailable(tableId: Int, bookingTime: Date): Boolean {
        // Kiểm tra xem khung giờ có trong danh sách và có khả dụng không
        val slot = _tableBookingSlots.value.entries.find { entry ->
            val slotCalendar = Calendar.getInstance()
            slotCalendar.time = entry.key
            
            val bookingCalendar = Calendar.getInstance()
            bookingCalendar.time = bookingTime
            
            slotCalendar.get(Calendar.HOUR_OF_DAY) == bookingCalendar.get(Calendar.HOUR_OF_DAY)
        }
        
        return slot?.value ?: false
    }

    // Kiểm tra xem bàn đã được đặt vào một khung giờ cụ thể chưa
    private fun checkTableAvailability(tableId: Int, timeSlot: Date): Flow<Boolean> {
        return flow {
            val calendar = Calendar.getInstance()
            calendar.time = timeSlot
            
            // Tạo khoảng thời gian +/- 1 giờ
            val startTime = Calendar.getInstance().apply {
                time = timeSlot
                add(Calendar.HOUR, -1)
            }.time
            
            val endTime = Calendar.getInstance().apply {
                time = timeSlot
                add(Calendar.HOUR, 1)
            }.time
            
            val isBooked = bookingDao.isTableBooked(tableId, startTime, endTime)
            emit(isBooked)
        }.catch { e ->
            Log.e("BookingViewModel", "Error checking table availability: ${e.message}")
            emit(false) // Assume table is free if there's an error
        }
    }

    class Factory(
        private val bookingDao: BookingDao,
        private val tableDao: TableDao
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BookingViewModel(bookingDao, tableDao) as T
        }
    }
}
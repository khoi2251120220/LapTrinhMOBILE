package com.example.restaurantmanage.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.dao.TableDao
import com.example.restaurantmanage.data.local.dao.BookingDao
import com.example.restaurantmanage.data.local.entity.TableEntity
import com.example.restaurantmanage.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    init {
        // Load initial table order counts
        updateTableOrderCounts()
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
    
    // Load booking for a specific table
    fun loadBookingForTable(tableId: Int) {
        viewModelScope.launch {
            // Lấy booking có trạng thái CONFIRMED cho bàn này
            val booking = bookingDao.getActiveBookingForTable(tableId)
            _selectedTableBooking.value = booking
        }
    }
    
    // Hủy đặt bàn
    fun cancelBooking(booking: BookingEntity) {
        viewModelScope.launch {
            // Cập nhật trạng thái đặt bàn thành CANCELLED
            bookingDao.updateBookingStatus(booking.id, "CANCELLED")
            
            // Đặt lại trạng thái bàn thành AVAILABLE
            tableDao.updateTableStatus(booking.tableId, "AVAILABLE")
            
            // Reset selected booking
            _selectedTableBooking.value = null
        }
    }

    fun addTable(name: String, capacity: Int) {
        viewModelScope.launch {
            val table = TableEntity(
                name = name,
                capacity = capacity
            )
            tableDao.insertTable(table)
        }
    }
    
    fun addTableWithImage(name: String, capacity: Int, imagePath: String) {
        viewModelScope.launch {
            val table = TableEntity(
                name = name,
                capacity = capacity,
                image = imagePath
            )
            tableDao.insertTable(table)
        }
    }

    fun updateTableStatus(tableId: Int, status: String) {
        viewModelScope.launch {
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
            
            // Update this in database/repository if you have a specific field for this
            // Here we're just keeping it in memory, but you should persist it
        }
    }
    
    private fun updateTableOrderCounts() {
        viewModelScope.launch {
            // This would typically come from a repository or database
            // For now, we'll initialize with zeroes for all tables
            val tableList = tableDao.getAllTablesAsList()
            val countMap = tableList.associate { it.id to 0 }
            _tableOrderCount.value = countMap
        }
    }

    fun deleteTable(table: TableEntity) {
        viewModelScope.launch {
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
            emit(bookingDao.isTableBooked(tableId, startTime, endTime))
        }
    }
    
    fun searchTablesByName(query: String): Flow<List<TableEntity>> {
        return tableDao.searchTablesByName("%$query%")
    }

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
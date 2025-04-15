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

    fun getFilteredTables(tabIndex: Int): Flow<List<TableEntity>> {
        return when (tabIndex) {
            0 -> tables
            1 -> availableTables
            2 -> reservedTables
            3 -> occupiedTables
            else -> tables
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

    fun updateTableStatus(tableId: Int, status: String) {
        viewModelScope.launch {
            tableDao.updateTableStatus(tableId, status)
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
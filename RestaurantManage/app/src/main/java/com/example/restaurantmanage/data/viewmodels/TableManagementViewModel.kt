package com.example.restaurantmanage.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.models.Table
import com.example.restaurantmanage.data.models.TableStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class TableManagementViewModel : ViewModel() {
    private val _tables = MutableStateFlow<List<Table>>(emptyList())
    val tables: StateFlow<List<Table>> = _tables.asStateFlow()

    private val _availableTables = MutableStateFlow<List<Table>>(emptyList())
    val availableTables: StateFlow<List<Table>> = _availableTables.asStateFlow()

    private val _reservedTables = MutableStateFlow<List<Table>>(emptyList())
    val reservedTables: StateFlow<List<Table>> = _reservedTables.asStateFlow()

    private val _occupiedTables = MutableStateFlow<List<Table>>(emptyList())
    val occupiedTables: StateFlow<List<Table>> = _occupiedTables.asStateFlow()

    init {
        loadTableData()
    }

    private fun loadTableData() {
        viewModelScope.launch {
            // Mock data
            val currentTime = Calendar.getInstance().time
            val calendar = Calendar.getInstance()

            // Thiết lập thời gian cho đặt bàn
            calendar.add(Calendar.HOUR, 1)
            val reservation1Time = calendar.time

            calendar.add(Calendar.HOUR, 1)
            val reservation2Time = calendar.time

            // Thiết lập thời gian bắt đầu cho order hiện tại
            calendar.add(Calendar.HOUR, -3)
            val order1StartTime = calendar.time

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
                    capacity = 2,
                    status = TableStatus.OCCUPIED,
                    currentOrder = com.example.restaurantmanage.data.models.Order(
                        id = 1,
                        startTime = order1StartTime,
                        items = listOf("Tôm xào chua ngọt", "Cơm trắng", "Canh chua"),
                        totalAmount = 250000.0,
                        status = "Đang phục vụ"
                    )
                ),
                Table(
                    id = 3,
                    name = "Bàn 03",
                    capacity = 6,
                    status = TableStatus.RESERVED,
                    reservation = com.example.restaurantmanage.data.models.Reservation(
                        id = 1,
                        customerName = "Nguyễn Văn A",
                        phoneNumber = "0912345678",
                        numberOfGuests = 5,
                        time = reservation1Time,
                        note = "Kỷ niệm sinh nhật"
                    )
                ),
                Table(
                    id = 4,
                    name = "Bàn 04",
                    capacity = 4,
                    status = TableStatus.AVAILABLE
                ),
                Table(
                    id = 5,
                    name = "Bàn 05",
                    capacity = 8,
                    status = TableStatus.RESERVED,
                    reservation = com.example.restaurantmanage.data.models.Reservation(
                        id = 2,
                        customerName = "Trần Thị B",
                        phoneNumber = "0987654321",
                        numberOfGuests = 7,
                        time = reservation2Time,
                        note = "Gần cửa sổ"
                    )
                ),
                Table(
                    id = 6,
                    name = "Bàn 06",
                    capacity = 2,
                    status = TableStatus.AVAILABLE
                ),
                Table(
                    id = 7,
                    name = "Bàn 07",
                    capacity = 4,
                    status = TableStatus.AVAILABLE
                ),
                Table(
                    id = 8,
                    name = "Bàn VIP 01",
                    capacity = 10,
                    status = TableStatus.OCCUPIED,
                    currentOrder = com.example.restaurantmanage.data.models.Order(
                        id = 2,
                        startTime = order1StartTime,
                        items = listOf("Gà nướng", "Lẩu hải sản", "Rau muống xào", "Cơm trắng"),
                        totalAmount = 850000.0,
                        status = "Đang phục vụ"
                    )
                ),
                Table(
                    id = 9,
                    name = "Bàn 09",
                    capacity = 4,
                    status = TableStatus.AVAILABLE
                ),
                Table(
                    id = 10,
                    name = "Bàn 10",
                    capacity = 6,
                    status = TableStatus.AVAILABLE
                ),
                Table(
                    id = 11,
                    name = "Bàn 11",
                    capacity = 4,
                    status = TableStatus.AVAILABLE
                ),
                Table(
                    id = 12,
                    name = "Bàn 12",
                    capacity = 2,
                    status = TableStatus.AVAILABLE
                )
            )

            _tables.value = mockTables
            updateFilteredTables(mockTables)
        }
    }

    private fun updateFilteredTables(tables: List<Table>) {
        _availableTables.value = tables.filter { it.status == TableStatus.AVAILABLE }
        _reservedTables.value = tables.filter { it.status == TableStatus.RESERVED }
        _occupiedTables.value = tables.filter { it.status == TableStatus.OCCUPIED }
    }

    fun getFilteredTables(selectedTab: Int): List<Table> {
        return when (selectedTab) {
            1 -> _availableTables.value
            2 -> _reservedTables.value
            3 -> _occupiedTables.value
            else -> _tables.value
        }
    }

    fun getAvailableTables(): List<Table> {
        return _availableTables.value
    }
}
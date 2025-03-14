package com.example.restaurantmanage.data.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class DashboardData(
    val totalRevenue: Double = 45678.90,
    val revenueGrowth: Int = 20,
    val profit: Int = 2405,
    val profitGrowth: Int = 33,
    val dailyRevenue: List<Pair<Int, Double>> = listOf(
        23 to 30000.0,
        24 to 32000.0,
        25 to 34000.0,
        26 to 36000.0,
        27 to 38000.0,
        28 to 40000.0,
        29 to 42000.0,
        30 to 50000.0
    ),
    val loyalCustomers: List<String> = listOf("Nguyen Van A", "Tran Thi B")
)

class DashboardViewModel : ViewModel() {
    private val _data = mutableStateOf(DashboardData())
    val data = _data

    // Thêm logic cập nhật dữ liệu nếu cần (ví dụ: từ API)
}
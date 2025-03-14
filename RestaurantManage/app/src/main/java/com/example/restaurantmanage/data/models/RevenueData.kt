package com.example.restaurantmanage.data.models

data class RevenueData(
    val totalRevenue: Double,
    val profit: Double,
    val revenueGrowth: Double,
    val profitGrowth: Double,
    val dailyRevenue: List<Pair<Int, Double>>,
    val loyalCustomers: List<String>
)
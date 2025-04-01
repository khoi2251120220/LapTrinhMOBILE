package com.example.restaurantmanage.data.models

enum class TableStatus {
    AVAILABLE, RESERVED, OCCUPIED
}

data class Table(
    val id: Int,
    val name: String,
    val capacity: Int,
    val status: TableStatus,
    val reservation: Reservation? = null,
    val currentOrder: Order? = null
)

data class Reservation(
    val id: Int,
    val customerName: String,
    val phoneNumber: String,
    val numberOfGuests: Int,
    val time: java.util.Date,
    val note: String = ""
)

data class Order(
    val id: Int,
    val startTime: java.util.Date,
    val items: List<String>,
    val totalAmount: Double,
    val status: String
)


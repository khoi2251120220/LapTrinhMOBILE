package com.example.restaurantmanage.data.viewmodels

import androidx.lifecycle.ViewModel
import com.example.restaurantmanage.data.models.BookingData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BookingViewModel : ViewModel() {
    private val _data = MutableStateFlow(
        listOf(
            BookingData(
                locationName = "Location 1",
                rating = 4.8f,
                reviewCount = 500,
                price = "1,000,000/phòng",
                imageUrl = ""
            ),
            BookingData(
                locationName = "Location 2",
                rating = 4.7f,
                reviewCount = 800,
                price = "1,500,000/phòng",
                imageUrl = ""
            )
        )
    )
    val data: StateFlow<List<BookingData>> = _data
}
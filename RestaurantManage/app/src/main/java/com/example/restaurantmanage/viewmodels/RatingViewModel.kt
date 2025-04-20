package com.example.restaurantmanage.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.restaurantmanage.data.local.RestaurantDatabase
import com.example.restaurantmanage.data.local.entity.RatingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RatingViewModel(application: Application) : AndroidViewModel(application) {
    private val ratingDao = RestaurantDatabase.getDatabase(application).ratingDao()
    
    private val _ratingSubmitted = MutableLiveData<Boolean>(false)
    val ratingSubmitted: LiveData<Boolean> = _ratingSubmitted
    
    val allRatings: Flow<List<RatingEntity>> = ratingDao.getAllRatings()
    
    fun submitRating(customerName: String, rating: Int, feedback: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val ratingEntity = RatingEntity(
                customerName = customerName,
                rating = rating,
                feedback = feedback
            )
            
            val id = ratingDao.insertRating(ratingEntity)
            if (id > 0) {
                _ratingSubmitted.postValue(true)
            }
        }
    }
    
    fun resetSubmissionState() {
        _ratingSubmitted.value = false
    }
} 
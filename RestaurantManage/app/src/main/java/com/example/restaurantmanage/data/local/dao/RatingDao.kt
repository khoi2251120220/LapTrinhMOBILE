package com.example.restaurantmanage.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.restaurantmanage.data.local.entity.RatingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RatingDao {
    @Insert
    suspend fun insertRating(rating: RatingEntity): Long
    
    @Query("SELECT * FROM ratings ORDER BY created_at DESC")
    fun getAllRatings(): Flow<List<RatingEntity>>
    
    @Query("SELECT * FROM ratings WHERE id = :id")
    suspend fun getRatingById(id: Int): RatingEntity?
} 
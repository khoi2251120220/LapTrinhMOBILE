package com.example.restaurantmanage.data.local.dao

import androidx.room.*
import com.example.restaurantmanage.data.local.entity.ReservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {
    @Query("SELECT * FROM reservations")
    fun getAllReservations(): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getReservationById(id: Int): ReservationEntity?

    @Query("SELECT * FROM reservations WHERE table_id = :tableId")
    fun getReservationsByTable(tableId: Int): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE time BETWEEN :startTime AND :endTime")
    fun getReservationsByTimeRange(startTime: Long, endTime: Long): Flow<List<ReservationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservations(reservations: List<ReservationEntity>)

    @Update
    suspend fun updateReservation(reservation: ReservationEntity)

    @Delete
    suspend fun deleteReservation(reservation: ReservationEntity)
} 
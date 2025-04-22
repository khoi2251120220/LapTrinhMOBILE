package com.example.restaurantmanage.data.local.dao

import androidx.room.*
import com.example.restaurantmanage.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun getBookingById(id: String): BookingEntity?
    
    @Query("SELECT * FROM bookings WHERE userId = :userId")
    fun getBookingsByUserId(userId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE tableId = :tableId")
    fun getBookingsByTableId(tableId: Int): Flow<List<BookingEntity>>
    
    @Query("SELECT * FROM bookings WHERE status = :status")
    fun getBookingsByStatus(status: String): Flow<List<BookingEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookings WHERE tableId = :tableId AND bookingTime BETWEEN :startTime AND :endTime AND status != 'CANCELLED')")
    suspend fun isTableBooked(tableId: Int, startTime: Date, endTime: Date): Boolean
    
    @Query("SELECT * FROM bookings WHERE tableId = :tableId AND status = 'CONFIRMED' LIMIT 1")
    suspend fun getActiveBookingForTable(tableId: Int): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)
    
    @Query("UPDATE bookings SET status = :status WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: String, status: String)
}
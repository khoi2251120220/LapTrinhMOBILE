package com.example.restaurantmanage.data.local.dao

import androidx.room.*
import com.example.restaurantmanage.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings")
    fun getAllBookings(): Flow<List<BookingEntity>>
    
    @Query("SELECT * FROM bookings WHERE userId = :userId AND status != 'CANCELLED'")
    fun getBookingsByUserId(userId: String): Flow<List<BookingEntity>>
    
    @Query("SELECT * FROM bookings WHERE userId = :userId AND status != 'CANCELLED'")
    suspend fun getBookingsByUserIdAsList(userId: String): List<BookingEntity>
    
    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: String): BookingEntity?
    
    @Query("SELECT COUNT(*) FROM bookings WHERE tableId = :tableId AND status = 'CONFIRMED'")
    suspend fun getActiveBookingCountForTable(tableId: Int): Int
    
    @Query("SELECT * FROM bookings WHERE tableId = :tableId AND status = 'CONFIRMED' ORDER BY bookingTime DESC LIMIT 1")
    suspend fun getActiveBookingForTable(tableId: Int): BookingEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)
    
    @Update
    suspend fun updateBooking(booking: BookingEntity)
    
    @Query("UPDATE bookings SET status = :status WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: String, status: String)
    
    @Delete
    suspend fun deleteBooking(booking: BookingEntity)
    
    @Query("SELECT EXISTS(SELECT 1 FROM bookings WHERE tableId = :tableId AND bookingTime BETWEEN :startTime AND :endTime AND status != 'CANCELLED')")
    suspend fun isTableBooked(tableId: Int, startTime: Date, endTime: Date): Boolean
    
    @Query("SELECT * FROM bookings WHERE bookingTime BETWEEN :startOfDay AND :endOfDay")
    suspend fun getBookingsForDate(startOfDay: Date, endOfDay: Date): List<BookingEntity>
    
    @Query("SELECT * FROM bookings WHERE bookingTime BETWEEN :startTime AND :endTime")
    suspend fun getBookingsBetweenDates(startTime: Date, endTime: Date): List<BookingEntity>
    
    @Query("SELECT * FROM bookings WHERE status = 'CONFIRMED' ORDER BY bookingTime DESC")
    fun getActiveBookings(): Flow<List<BookingEntity>>
    
    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()
}
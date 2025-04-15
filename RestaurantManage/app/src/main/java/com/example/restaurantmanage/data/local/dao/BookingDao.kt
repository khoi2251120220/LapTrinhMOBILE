package com.example.restaurantmanage.data.local.dao

import androidx.room.*
import com.example.restaurantmanage.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE status = :status")
    fun getBookingsByStatus(status: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE tableId = :tableId")
    fun getBookingsByTableId(tableId: Int): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE time BETWEEN :startDate AND :endDate")
    fun getBookingsByDateRange(startDate: Date, endDate: Date): Flow<List<BookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status WHERE id = :bookingId")
    suspend fun updateBookingStatus(bookingId: Int, status: String)

    @Transaction
    @Query("""
        SELECT EXISTS (
            SELECT 1 FROM bookings 
            WHERE tableId = :tableId 
            AND time BETWEEN :startTime AND :endTime
            AND status IN ('PENDING', 'CONFIRMED')
        )
    """)
    suspend fun isTableBooked(tableId: Int, startTime: Date, endTime: Date): Boolean
}
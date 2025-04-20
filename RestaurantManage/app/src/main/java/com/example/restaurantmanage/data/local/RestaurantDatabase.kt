package com.example.restaurantmanage.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.restaurantmanage.data.local.converter.Converters
import com.example.restaurantmanage.data.local.dao.*
import com.example.restaurantmanage.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
//        UserEntity::class,
        CategoryEntity::class,
        MenuItemEntity::class,
        TableEntity::class,
        ReservationEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        BookingEntity::class,
        CartItemEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RestaurantDatabase : RoomDatabase() {
//    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun tableDao(): TableDao
    abstract fun reservationDao(): ReservationDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun bookingDao(): BookingDao
    abstract fun cartItemDao(): CartItemDao

    companion object {
        @Volatile
        private var INSTANCE: RestaurantDatabase? = null
        
        // Migration từ version 3 sang 4: thêm cột image vào bảng tables
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Thêm cột image vào bảng tables
                database.execSQL("ALTER TABLE tables ADD COLUMN image TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): RestaurantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RestaurantDatabase::class.java,
                    "restaurant_database"
                )
                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_3_4)
                .build()

                INSTANCE = instance

                // Khởi tạo dữ liệu mẫu
                CoroutineScope(Dispatchers.IO).launch {
                    DatabaseInitializer.initializeData(
                        instance.categoryDao(),
                        instance.menuItemDao()
                    )
                }

                instance
            }
        }
    }
} 
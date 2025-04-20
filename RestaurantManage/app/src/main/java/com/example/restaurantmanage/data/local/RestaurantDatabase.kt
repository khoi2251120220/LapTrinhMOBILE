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
        CartItemEntity::class,
        RatingEntity::class
    ],
    version = 6,
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
    abstract fun ratingDao(): RatingDao

    companion object {
        @Volatile
        private var INSTANCE: RestaurantDatabase? = null
        
        // Migration từ version 3 sang 4: thêm cột image vào bảng tables
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Thêm cột image vào bảng tables
                db.execSQL("ALTER TABLE tables ADD COLUMN image TEXT NOT NULL DEFAULT ''")
            }
        }
        
        // Migration từ version 4 sang 5: thêm bảng ratings
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ratings` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`customer_name` TEXT NOT NULL, " +
                    "`rating` INTEGER NOT NULL, " +
                    "`feedback` TEXT NOT NULL, " +
                    "`created_at` INTEGER NOT NULL)"
                )
            }
        }
        
        // Migration từ version 5 sang 6: thêm cột customer_name vào bảng orders
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE orders ADD COLUMN customer_name TEXT NOT NULL DEFAULT 'Khách hàng'")
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
                .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
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
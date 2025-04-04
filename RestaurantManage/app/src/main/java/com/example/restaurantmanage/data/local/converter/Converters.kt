package com.example.restaurantmanage.data.local.converter

import androidx.room.TypeConverter
import com.example.restaurantmanage.data.models.TableStatus
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTableStatus(value: TableStatus): String {
        return value.name
    }

    @TypeConverter
    fun toTableStatus(value: String): TableStatus {
        return TableStatus.valueOf(value)
    }
} 
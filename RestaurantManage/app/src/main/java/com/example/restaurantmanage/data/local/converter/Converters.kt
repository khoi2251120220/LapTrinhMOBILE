package com.example.restaurantmanage.data.local.converter

import androidx.room.TypeConverter
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import com.example.restaurantmanage.data.models.TableStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    fun fromTableStatus(value: TableStatus?): String? {
        return value?.name
    }

    @TypeConverter
    fun toTableStatus(value: String?): TableStatus? {
        return value?.let { TableStatus.valueOf(it) }
    }

    private val gson = Gson()

    @TypeConverter
    fun fromMenuItemList(value: List<MenuItemEntity>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMenuItemList(value: String?): List<MenuItemEntity>? {
        val listType = object : TypeToken<List<MenuItemEntity>>() {}.type
        return gson.fromJson(value, listType)
    }
}
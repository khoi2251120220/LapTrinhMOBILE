package com.example.restaurantmanage.data.local.dao

import androidx.room.*
import com.example.restaurantmanage.data.local.entity.MenuItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuItemDao {
    @Query("SELECT * FROM menu_items")
    fun getAllMenuItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE category_id = :categoryId")
    fun getMenuItemsByCategory(categoryId: Int): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items WHERE id = :id")
    suspend fun getMenuItemById(id: String): MenuItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(menuItem: MenuItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(menuItems: List<MenuItemEntity>)

    @Update
    suspend fun updateMenuItem(menuItem: MenuItemEntity)

    @Delete
    suspend fun deleteMenuItem(menuItem: MenuItemEntity)

    @Query("SELECT * FROM menu_items WHERE in_stock = 1")
    fun getAvailableMenuItems(): Flow<List<MenuItemEntity>>
} 
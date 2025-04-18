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
    fun getMenuItemById(id: String): Flow<MenuItemEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(menuItem: MenuItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(menuItems: List<MenuItemEntity>)

    @Update
    suspend fun updateMenuItem(menuItem: MenuItemEntity)

    @Delete
    suspend fun deleteMenuItem(menuItem: MenuItemEntity)

    @Query("UPDATE menu_items SET name = :name, price = :price WHERE id = :id")
    suspend fun updateMenuItem(id: String, name: String, price: Double)

    @Query("UPDATE menu_items SET image = CASE WHEN :image IS NULL THEN image ELSE :image END, description = CASE WHEN :description IS NULL THEN description ELSE :description END WHERE id = :id")
    suspend fun updateItemDetails(id: String, image: String?, description: String?)

    @Query("UPDATE menu_items SET in_stock = :inStock WHERE id = :id")
    suspend fun updateItemStock(id: String, inStock: Boolean)

    @Query("SELECT * FROM menu_items WHERE in_stock = 1")
    fun getAvailableMenuItems(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items ORDER BY order_count DESC LIMIT :limit")
    fun getTopSellingItems(limit: Int): Flow<List<MenuItemEntity>>
} 
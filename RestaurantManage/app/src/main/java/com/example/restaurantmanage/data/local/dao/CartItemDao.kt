package com.example.restaurantmanage.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.restaurantmanage.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItemEntity>>

    @Insert
    suspend fun insertCartItem(cartItem: CartItemEntity)

    @Update
    suspend fun updateCartItem(cartItem: CartItemEntity)

    @Query("SELECT * FROM cart_items WHERE menuItemId = :menuItemId")
    suspend fun getCartItemsByMenuId(menuItemId: String): List<CartItemEntity>

    @Query("DELETE FROM cart_items WHERE menuItemId = :menuItemId")
    suspend fun deleteCartItemByMenuId(menuItemId: String)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
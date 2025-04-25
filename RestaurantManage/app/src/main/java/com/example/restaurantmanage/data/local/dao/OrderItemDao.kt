package com.example.restaurantmanage.data.local.dao

import androidx.room.*
import com.example.restaurantmanage.data.local.entity.OrderItemEntity
import com.example.restaurantmanage.data.local.entity.OrderItemWithMenuDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsByOrderId(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE menu_item_id = :menuItemId")
    fun getOrderItemsByMenuItemId(menuItemId: String): Flow<List<OrderItemEntity>>
    
    @Transaction
    @Query("SELECT oi.orderId, oi.menu_item_id as menuItemId, mi.name as menuItemName, " +
           "oi.quantity, oi.price, oi.notes, mi.image " +
           "FROM order_items oi " + 
           "INNER JOIN menu_items mi ON oi.menu_item_id = mi.id " +
           "WHERE oi.orderId = :orderId")
    suspend fun getOrderItemsWithMenuDetails(orderId: String): List<OrderItemWithMenuDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(orderItems: List<OrderItemEntity>)

    @Update
    suspend fun updateOrderItem(orderItem: OrderItemEntity)

    @Delete
    suspend fun deleteOrderItem(orderItem: OrderItemEntity)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItemsByOrderId(orderId: String)
} 
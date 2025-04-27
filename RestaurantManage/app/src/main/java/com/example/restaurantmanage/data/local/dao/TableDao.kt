package com.example.restaurantmanage.data.local.dao

import androidx.room.*
import com.example.restaurantmanage.data.local.entity.TableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {
    @Query("SELECT * FROM tables")
    fun getAllTables(): Flow<List<TableEntity>>
    
    @Query("SELECT * FROM tables")
    suspend fun getAllTablesAsList(): List<TableEntity>

    @Query("SELECT * FROM tables WHERE status = :status")
    fun getTablesByStatus(status: String): Flow<List<TableEntity>>

    @Query("SELECT * FROM tables WHERE status = :status")
    suspend fun getTablesByStatusAsList(status: String): List<TableEntity>

    @Query("SELECT * FROM tables WHERE id = :tableId")
    suspend fun getTableById(tableId: Int): TableEntity?
    
    @Query("SELECT * FROM tables WHERE name LIKE :query")
    fun searchTablesByName(query: String): Flow<List<TableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTable(table: TableEntity): Long

    @Update
    suspend fun updateTable(table: TableEntity)

    @Delete
    suspend fun deleteTable(table: TableEntity)

    @Query("UPDATE tables SET status = :status WHERE id = :tableId")
    suspend fun updateTableStatus(tableId: Int, status: String)

    @Query("DELETE FROM tables WHERE id = :tableId")
    suspend fun deleteTable(tableId: Int)

    @Query("DELETE FROM tables")
    suspend fun deleteAllTables()

    @Query("SELECT COUNT(*) FROM tables")
    suspend fun getTableCount(): Int
} 
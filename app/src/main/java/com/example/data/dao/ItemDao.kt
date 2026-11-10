package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items WHERE counterId = :counterId ORDER BY updatedAt DESC")
    fun getItemsForCounter(counterId: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE counterId = :counterId AND name LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchItemsInCounter(counterId: String, query: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items ORDER BY updatedAt DESC")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE itemId = :itemId LIMIT 1")
    suspend fun getItemById(itemId: String): ItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)

    @Query("DELETE FROM items WHERE itemId = :itemId")
    suspend fun deleteItemById(itemId: String)

    @Query("UPDATE items SET isPacked = :isPacked, updatedAt = :updatedAt WHERE itemId = :itemId")
    suspend fun updatePackingStatus(itemId: String, isPacked: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE items SET isPacked = 0, updatedAt = :updatedAt WHERE counterId = :counterId")
    suspend fun resetPackingStatusForCounter(counterId: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE items SET isPacked = 0, updatedAt = :updatedAt")
    suspend fun resetPackingStatusForAll(updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM items")
    fun getTotalItemCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(quantity), 0.0) FROM items")
    fun getTotalQuantity(): Flow<Double>
}

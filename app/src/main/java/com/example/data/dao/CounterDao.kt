package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CounterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {
    @Query("SELECT * FROM counters ORDER BY createdAt DESC")
    fun getAllCounters(): Flow<List<CounterEntity>>

    @Query("SELECT * FROM counters WHERE counterId = :counterId LIMIT 1")
    fun getCounterById(counterId: String): Flow<CounterEntity?>

    @Query("SELECT * FROM counters WHERE counterId = :counterId LIMIT 1")
    suspend fun getCounterByIdOnce(counterId: String): CounterEntity?

    @Query("SELECT COUNT(*) FROM counters")
    fun getCounterCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCounter(counter: CounterEntity)

    @Update
    suspend fun updateCounter(counter: CounterEntity)

    @Delete
    suspend fun deleteCounter(counter: CounterEntity)

    @Query("DELETE FROM counters WHERE counterId = :counterId")
    suspend fun deleteCounterById(counterId: String)

    @Query("DELETE FROM counters")
    suspend fun clearAll()
}

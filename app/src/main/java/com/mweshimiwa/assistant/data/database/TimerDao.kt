package com.mweshimiwa.assistant.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timer: TimerEntity): Long

    @Update
    suspend fun update(timer: TimerEntity)

    @Delete
    suspend fun delete(timer: TimerEntity)

    @Query("SELECT * FROM timers ORDER BY createdAt DESC")
    suspend fun getAll(): List<TimerEntity>

    @Query("SELECT * FROM timers WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TimerEntity?

    @Query("SELECT * FROM timers WHERE status = 'running' OR status = 'paused'")
    suspend fun getActive(): List<TimerEntity>

    @Query("SELECT * FROM timers WHERE status = 'completed' ORDER BY createdAt DESC")
    suspend fun getCompleted(): List<TimerEntity>

    @Query("UPDATE timers SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE timers SET remainingTime = :remaining WHERE id = :id")
    suspend fun updateRemainingTime(id: Long, remaining: Long)

    @Query("DELETE FROM timers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM timers WHERE status = 'completed'")
    suspend fun deleteCompleted()
}

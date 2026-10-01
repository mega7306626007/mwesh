package com.jarvis.assistant.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CommandUsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usage: CommandUsageEntity): Long

    @Query("SELECT * FROM command_usage WHERE command = :command LIMIT 1")
    suspend fun getByCommand(command: String): CommandUsageEntity?

    @Query("SELECT * FROM command_usage ORDER BY usageCount DESC")
    suspend fun getAll(): List<CommandUsageEntity>

    @Query("SELECT * FROM command_usage ORDER BY usageCount DESC LIMIT :limit")
    suspend fun getMostUsed(limit: Int): List<CommandUsageEntity>

    @Query("UPDATE command_usage SET usageCount = usageCount + 1, lastUsed = :timestamp WHERE command = :command")
    suspend fun incrementUsage(command: String, timestamp: Long)

    @Query("DELETE FROM command_usage WHERE command = :command")
    suspend fun deleteByCommand(command: String)

    @Query("DELETE FROM command_usage")
    suspend fun deleteAll()
}

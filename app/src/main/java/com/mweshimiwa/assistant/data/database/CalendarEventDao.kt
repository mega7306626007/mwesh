package com.mweshimiwa.assistant.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CalendarEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: CalendarEventEntity): Long

    @Update
    suspend fun update(event: CalendarEventEntity)

    @Delete
    suspend fun delete(event: CalendarEventEntity)

    @Query("SELECT * FROM calendar_events ORDER BY startTime ASC")
    suspend fun getAll(): List<CalendarEventEntity>

    @Query("SELECT * FROM calendar_events WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CalendarEventEntity?

    @Query("SELECT * FROM calendar_events WHERE startTime >= :start AND startTime <= :end ORDER BY startTime ASC")
    suspend fun getInRange(start: Long, end: Long): List<CalendarEventEntity>

    @Query("SELECT * FROM calendar_events WHERE startTime >= :currentTime ORDER BY startTime ASC")
    suspend fun getUpcoming(currentTime: Long): List<CalendarEventEntity>

    @Query("SELECT * FROM calendar_events WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY startTime ASC")
    suspend fun search(query: String): List<CalendarEventEntity>

    @Query("DELETE FROM calendar_events WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calendar_events")
    suspend fun deleteAll()
}

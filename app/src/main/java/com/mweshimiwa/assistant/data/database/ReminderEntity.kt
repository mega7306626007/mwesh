package com.mweshimiwa.assistant.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val time: Long,
    val recurrence: String = "none",
    val priority: Int = 1,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

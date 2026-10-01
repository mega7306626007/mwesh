package com.jarvis.assistant.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timers")
data class TimerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val duration: Long,
    val startTime: Long = 0,
    val status: String = "idle",
    val remainingTime: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

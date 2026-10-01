package com.jarvis.assistant.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val time: Long,
    val days: String = "",
    val enabled: Boolean = true,
    val label: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

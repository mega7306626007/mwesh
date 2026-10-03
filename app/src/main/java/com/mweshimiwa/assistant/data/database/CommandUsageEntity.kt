package com.mweshimiwa.assistant.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "command_usage")
data class CommandUsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val command: String,
    val usageCount: Int = 0,
    val lastUsed: Long = System.currentTimeMillis()
)

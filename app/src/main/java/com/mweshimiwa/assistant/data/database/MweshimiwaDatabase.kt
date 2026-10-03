package com.mweshimiwa.assistant.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ConversationEntity::class,
        MemoryEntity::class,
        ReminderEntity::class,
        NoteEntity::class,
        TimerEntity::class,
        CommandUsageEntity::class,
        AlarmEntity::class,
        CalendarEventEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MweshimiwaDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun memoryDao(): MemoryDao
    abstract fun reminderDao(): ReminderDao
    abstract fun noteDao(): NoteDao
    abstract fun timerDao(): TimerDao
    abstract fun commandUsageDao(): CommandUsageDao
    abstract fun alarmDao(): AlarmDao
    abstract fun calendarEventDao(): CalendarEventDao

    companion object {
        @Volatile
        private var INSTANCE: MweshimiwaDatabase? = null

        fun getInstance(context: Context): MweshimiwaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MweshimiwaDatabase::class.java,
                    "mweshimiwa_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}

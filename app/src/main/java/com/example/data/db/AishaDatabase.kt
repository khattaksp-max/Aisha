package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [MemoryEntity::class, ChatMessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AishaDatabase : RoomDatabase() {

    abstract fun aishaDao(): AishaDao

    companion object {
        @Volatile
        private var INSTANCE: AishaDatabase? = null

        fun getInstance(context: Context): AishaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AishaDatabase::class.java,
                    "aisha_companion.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

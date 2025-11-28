package com.example.journal.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: JournalDatabase? = null
    
    fun getDatabase(context: Context): JournalDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                JournalDatabase::class.java,
                JournalDatabase.DATABASE_NAME
            )
            .fallbackToDestructiveMigration() // For now, allow schema changes
            .build()
            INSTANCE = instance
            instance
        }
    }
}

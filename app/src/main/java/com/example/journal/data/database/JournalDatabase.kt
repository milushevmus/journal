package com.example.journal.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.journal.data.model.JournalEntry

@Database(
    entities = [JournalEntry::class],
    version = 1,
    exportSchema = false
)
abstract class JournalDatabase : RoomDatabase() {
    abstract fun journalEntryDao(): JournalEntryDao
    
    companion object {
        const val DATABASE_NAME = "journal_database"
    }
}

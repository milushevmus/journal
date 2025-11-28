package com.example.journal.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.journal.data.model.Journal
import com.example.journal.data.model.JournalEntry

@Database(
    entities = [Journal::class, JournalEntry::class],
    version = 3,
    exportSchema = false
)
abstract class JournalDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun journalEntryDao(): JournalEntryDao
    
    companion object {
        const val DATABASE_NAME = "journal_database"
    }
}

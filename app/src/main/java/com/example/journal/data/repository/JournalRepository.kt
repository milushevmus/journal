package com.example.journal.data.repository

import com.example.journal.data.database.JournalEntryDao
import com.example.journal.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val journalEntryDao: JournalEntryDao) {
    
    fun getAllEntries(): Flow<List<JournalEntry>> = journalEntryDao.getAllEntries()
    
    suspend fun getEntryById(id: Long): JournalEntry? = journalEntryDao.getEntryById(id)
    
    fun getEntriesByDate(startOfDay: Long, endOfDay: Long): Flow<List<JournalEntry>> =
        journalEntryDao.getEntriesByDate(startOfDay, endOfDay)
    
    suspend fun insertEntry(entry: JournalEntry): Long = journalEntryDao.insertEntry(entry)
    
    suspend fun updateEntry(entry: JournalEntry) = journalEntryDao.updateEntry(entry)
    
    suspend fun deleteEntry(entry: JournalEntry) = journalEntryDao.deleteEntry(entry)
    
    suspend fun deleteEntryById(id: Long) = journalEntryDao.deleteEntryById(id)
}

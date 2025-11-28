package com.example.journal.data.repository

import com.example.journal.data.database.JournalDao
import com.example.journal.data.database.JournalEntryDao
import com.example.journal.data.model.Journal
import com.example.journal.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

class JournalRepository(
    private val journalDao: JournalDao,
    private val journalEntryDao: JournalEntryDao
) {
    // Journal operations
    fun getAllJournals(): Flow<List<Journal>> = journalDao.getAllJournals()
    
    suspend fun getJournalById(id: Long): Journal? = journalDao.getJournalById(id)
    
    fun getDeletedJournals(): Flow<List<Journal>> = journalDao.getDeletedJournals()
    
    suspend fun insertJournal(journal: Journal): Long = journalDao.insertJournal(journal)
    
    suspend fun updateJournal(journal: Journal) = journalDao.updateJournal(journal)
    
    suspend fun deleteJournal(journal: Journal) = journalDao.deleteJournal(journal)
    
    suspend fun deleteJournalById(id: Long) = journalDao.deleteJournalById(id)
    
    // Entry operations
    fun getAllEntries(): Flow<List<JournalEntry>> = journalEntryDao.getAllEntries()
    
    suspend fun getEntryById(id: Long): JournalEntry? = journalEntryDao.getEntryById(id)
    
    fun getEntriesByDate(startOfDay: Long, endOfDay: Long): Flow<List<JournalEntry>> =
        journalEntryDao.getEntriesByDate(startOfDay, endOfDay)
    
    fun getEntriesByJournalId(journalId: Long): Flow<List<JournalEntry>> =
        journalEntryDao.getEntriesByJournalId(journalId)
    
    suspend fun insertEntry(entry: JournalEntry): Long = journalEntryDao.insertEntry(entry)
    
    suspend fun updateEntry(entry: JournalEntry) = journalEntryDao.updateEntry(entry)
    
    suspend fun deleteEntry(entry: JournalEntry) = journalEntryDao.deleteEntry(entry)
    
    suspend fun deleteEntryById(id: Long) = journalEntryDao.deleteEntryById(id)
}

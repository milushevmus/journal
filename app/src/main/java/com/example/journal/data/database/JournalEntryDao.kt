package com.example.journal.data.database

import androidx.room.*
import com.example.journal.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {
    
    @Query("SELECT * FROM journal_entries ORDER BY date DESC, createdAt DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>
    
    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): JournalEntry?
    
    @Query("SELECT * FROM journal_entries WHERE date >= :startOfDay AND date < :endOfDay ORDER BY createdAt DESC")
    fun getEntriesByDate(startOfDay: Long, endOfDay: Long): Flow<List<JournalEntry>>
    
    @Query("SELECT * FROM journal_entries WHERE journalId = :journalId ORDER BY date DESC, createdAt DESC")
    fun getEntriesByJournalId(journalId: Long): Flow<List<JournalEntry>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntry): Long
    
    @Update
    suspend fun updateEntry(entry: JournalEntry)
    
    @Delete
    suspend fun deleteEntry(entry: JournalEntry)
    
    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)
}

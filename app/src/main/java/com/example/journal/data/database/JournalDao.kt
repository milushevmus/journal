package com.example.journal.data.database

import androidx.room.*
import com.example.journal.data.model.Journal
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    
    @Query("SELECT * FROM journals WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllJournals(): Flow<List<Journal>>
    
    @Query("SELECT * FROM journals WHERE id = :id")
    suspend fun getJournalById(id: Long): Journal?
    
    @Query("SELECT * FROM journals WHERE isDeleted = 1 ORDER BY createdAt DESC")
    fun getDeletedJournals(): Flow<List<Journal>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: Journal): Long
    
    @Update
    suspend fun updateJournal(journal: Journal)
    
    @Delete
    suspend fun deleteJournal(journal: Journal)
    
    @Query("DELETE FROM journals WHERE id = :id")
    suspend fun deleteJournalById(id: Long)
}


package com.example.journal.data.repository

import com.example.journal.data.database.JournalDao
import com.example.journal.data.database.JournalEntryDao
import com.example.journal.data.model.Journal
import com.example.journal.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class JournalRepositoryTest {

    @Mock
    private lateinit var journalDao: JournalDao

    @Mock
    private lateinit var journalEntryDao: JournalEntryDao

    private lateinit var repository: JournalRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = JournalRepository(journalDao, journalEntryDao)
    }

    @Test
    fun `test getAllJournals returns flow from dao`() = runTest {
        val journals = listOf(
            Journal(id = 1L, name = "Journal 1"),
            Journal(id = 2L, name = "Journal 2")
        )
        `when`(journalDao.getAllJournals()).thenReturn(flowOf(journals))

        val result = repository.getAllJournals().first()

        assertEquals(journals, result)
        verify(journalDao).getAllJournals()
    }

    @Test
    fun `test getJournalById returns journal from dao`() = runTest {
        val journal = Journal(id = 1L, name = "Test Journal")
        `when`(journalDao.getJournalById(1L)).thenReturn(journal)

        val result = repository.getJournalById(1L)

        assertEquals(journal, result)
        verify(journalDao).getJournalById(1L)
    }

    @Test
    fun `test getJournalById returns null when not found`() = runTest {
        `when`(journalDao.getJournalById(999L)).thenReturn(null)

        val result = repository.getJournalById(999L)

        assertNull(result)
        verify(journalDao).getJournalById(999L)
    }

    @Test
    fun `test getDeletedJournals returns flow from dao`() = runTest {
        val deletedJournals = listOf(
            Journal(id = 1L, name = "Deleted 1", isDeleted = true),
            Journal(id = 2L, name = "Deleted 2", isDeleted = true)
        )
        `when`(journalDao.getDeletedJournals()).thenReturn(flowOf(deletedJournals))

        val result = repository.getDeletedJournals().first()

        assertEquals(deletedJournals, result)
        verify(journalDao).getDeletedJournals()
    }

    @Test
    fun `test insertJournal calls dao and returns id`() = runTest {
        val journal = Journal(name = "New Journal")
        `when`(journalDao.insertJournal(journal)).thenReturn(1L)

        val result = repository.insertJournal(journal)

        assertEquals(1L, result)
        verify(journalDao).insertJournal(journal)
    }

    @Test
    fun `test updateJournal calls dao`() = runTest {
        val journal = Journal(id = 1L, name = "Updated Journal")
        
        repository.updateJournal(journal)

        verify(journalDao).updateJournal(journal)
    }

    @Test
    fun `test deleteJournal calls dao`() = runTest {
        val journal = Journal(id = 1L, name = "Journal to delete")
        
        repository.deleteJournal(journal)

        verify(journalDao).deleteJournal(journal)
    }

    @Test
    fun `test deleteJournalById calls dao`() = runTest {
        repository.deleteJournalById(1L)

        verify(journalDao).deleteJournalById(1L)
    }

    @Test
    fun `test getAllEntries returns flow from dao`() = runTest {
        val entries = listOf(
            JournalEntry(id = 1L, journalId = 1L, title = "Entry 1", content = "Content 1", date = 1000L),
            JournalEntry(id = 2L, journalId = 1L, title = "Entry 2", content = "Content 2", date = 2000L)
        )
        `when`(journalEntryDao.getAllEntries()).thenReturn(flowOf(entries))

        val result = repository.getAllEntries().first()

        assertEquals(entries, result)
        verify(journalEntryDao).getAllEntries()
    }

    @Test
    fun `test getEntryById returns entry from dao`() = runTest {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Test Entry", content = "Content", date = 1000L)
        `when`(journalEntryDao.getEntryById(1L)).thenReturn(entry)

        val result = repository.getEntryById(1L)

        assertEquals(entry, result)
        verify(journalEntryDao).getEntryById(1L)
    }

    @Test
    fun `test getEntriesByDate returns flow from dao`() = runTest {
        val startOfDay = 1000L
        val endOfDay = 2000L
        val entries = listOf(
            JournalEntry(id = 1L, journalId = 1L, title = "Entry", content = "Content", date = 1500L)
        )
        `when`(journalEntryDao.getEntriesByDate(startOfDay, endOfDay)).thenReturn(flowOf(entries))

        val result = repository.getEntriesByDate(startOfDay, endOfDay).first()

        assertEquals(entries, result)
        verify(journalEntryDao).getEntriesByDate(startOfDay, endOfDay)
    }

    @Test
    fun `test getEntriesByJournalId returns flow from dao`() = runTest {
        val journalId = 1L
        val entries = listOf(
            JournalEntry(id = 1L, journalId = journalId, title = "Entry 1", content = "Content", date = 1000L),
            JournalEntry(id = 2L, journalId = journalId, title = "Entry 2", content = "Content", date = 2000L)
        )
        `when`(journalEntryDao.getEntriesByJournalId(journalId)).thenReturn(flowOf(entries))

        val result = repository.getEntriesByJournalId(journalId).first()

        assertEquals(entries, result)
        verify(journalEntryDao).getEntriesByJournalId(journalId)
    }

    @Test
    fun `test insertEntry calls dao and returns id`() = runTest {
        val entry = JournalEntry(journalId = 1L, title = "New Entry", content = "Content", date = 1000L)
        `when`(journalEntryDao.insertEntry(entry)).thenReturn(1L)

        val result = repository.insertEntry(entry)

        assertEquals(1L, result)
        verify(journalEntryDao).insertEntry(entry)
    }

    @Test
    fun `test updateEntry calls dao`() = runTest {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Updated Entry", content = "Content", date = 1000L)
        
        repository.updateEntry(entry)

        verify(journalEntryDao).updateEntry(entry)
    }

    @Test
    fun `test deleteEntry calls dao`() = runTest {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Entry to delete", content = "Content", date = 1000L)
        
        repository.deleteEntry(entry)

        verify(journalEntryDao).deleteEntry(entry)
    }

    @Test
    fun `test deleteEntryById calls dao`() = runTest {
        repository.deleteEntryById(1L)

        verify(journalEntryDao).deleteEntryById(1L)
    }
}

package com.example.journal.data.repository

import com.example.journal.data.model.Journal
import com.example.journal.data.model.JournalEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.example.journal.testutil.FakeJournalDao
import com.example.journal.testutil.FakeJournalEntryDao

class JournalRepositoryTest {

    private lateinit var journalDao: FakeJournalDao
    private lateinit var journalEntryDao: FakeJournalEntryDao

    private lateinit var repository: JournalRepository

    @Before
    fun setup() {
        journalDao = FakeJournalDao()
        journalEntryDao = FakeJournalEntryDao()
        repository = JournalRepository(journalDao, journalEntryDao)
    }

    @Test
    fun `test getAllJournals returns flow from dao`() = runTest {
        val j1 = Journal(id = 1L, name = "Journal 1", createdAt = 100L, isDeleted = false)
        val j2 = Journal(id = 2L, name = "Journal 2", createdAt = 200L, isDeleted = false)
        journalDao.insertJournal(j1)
        journalDao.insertJournal(j2)

        val result = repository.getAllJournals().first()

        assertEquals(listOf(j2, j1), result)
        assertEquals(1, journalDao.getAllJournalsCalls)
    }

    @Test
    fun `test getJournalById returns journal from dao`() = runTest {
        val journal = Journal(id = 1L, name = "Test Journal", createdAt = 100L)
        journalDao.insertJournal(journal)

        val result = repository.getJournalById(1L)

        assertEquals(journal, result)
        assertEquals(1, journalDao.getJournalByIdCalls)
    }

    @Test
    fun `test getJournalById returns null when not found`() = runTest {
        val result = repository.getJournalById(999L)

        assertNull(result)
        assertEquals(1, journalDao.getJournalByIdCalls)
    }

    @Test
    fun `test getDeletedJournals returns flow from dao`() = runTest {
        val d1 = Journal(id = 1L, name = "Deleted 1", createdAt = 100L, isDeleted = true)
        val d2 = Journal(id = 2L, name = "Deleted 2", createdAt = 200L, isDeleted = true)
        journalDao.insertJournal(d1)
        journalDao.insertJournal(d2)

        val result = repository.getDeletedJournals().first()

        assertEquals(listOf(d2, d1), result)
        assertEquals(1, journalDao.getDeletedJournalsCalls)
    }

    @Test
    fun `test insertJournal calls dao and returns id`() = runTest {
        val journal = Journal(name = "New Journal", createdAt = 123L)

        val result = repository.insertJournal(journal)

        assertEquals(1L, result)
        assertEquals(1, journalDao.insertJournalCalls)
        assertTrue(journalDao.snapshot().any { it.id == 1L && it.name == "New Journal" })
    }

    @Test
    fun `test updateJournal calls dao`() = runTest {
        val journal = Journal(id = 1L, name = "Updated Journal", createdAt = 100L)
        
        repository.updateJournal(journal)

        assertEquals(1, journalDao.updateJournalCalls)
    }

    @Test
    fun `test deleteJournal calls dao`() = runTest {
        val journal = Journal(id = 1L, name = "Journal to delete", createdAt = 100L)
        
        repository.deleteJournal(journal)

        assertEquals(1, journalDao.deleteJournalCalls)
    }

    @Test
    fun `test deleteJournalById calls dao`() = runTest {
        repository.deleteJournalById(1L)

        assertEquals(1, journalDao.deleteJournalByIdCalls)
    }

    @Test
    fun `test getAllEntries returns flow from dao`() = runTest {
        val e1 = JournalEntry(id = 1L, journalId = 1L, title = "Entry 1", content = "Content 1", date = 1000L, createdAt = 10L)
        val e2 = JournalEntry(id = 2L, journalId = 1L, title = "Entry 2", content = "Content 2", date = 2000L, createdAt = 20L)
        journalEntryDao.insertEntry(e1)
        journalEntryDao.insertEntry(e2)

        val result = repository.getAllEntries().first()

        assertEquals(listOf(e2, e1), result)
        assertEquals(1, journalEntryDao.getAllEntriesCalls)
    }

    @Test
    fun `test getEntryById returns entry from dao`() = runTest {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Test Entry", content = "Content", date = 1000L, createdAt = 10L)
        journalEntryDao.insertEntry(entry)

        val result = repository.getEntryById(1L)

        assertEquals(entry, result)
        assertEquals(1, journalEntryDao.getEntryByIdCalls)
    }

    @Test
    fun `test getEntriesByDate returns flow from dao`() = runTest {
        val startOfDay = 1000L
        val endOfDay = 2000L
        val e1 = JournalEntry(id = 1L, journalId = 1L, title = "Entry", content = "Content", date = 1500L, createdAt = 10L)
        val e2 = JournalEntry(id = 2L, journalId = 1L, title = "Out of range", content = "Content", date = 2500L, createdAt = 20L)
        journalEntryDao.insertEntry(e1)
        journalEntryDao.insertEntry(e2)

        val result = repository.getEntriesByDate(startOfDay, endOfDay).first()

        assertEquals(listOf(e1), result)
        assertEquals(1, journalEntryDao.getEntriesByDateCalls)
    }

    @Test
    fun `test getEntriesByJournalId returns flow from dao`() = runTest {
        val journalId = 1L
        val e1 = JournalEntry(id = 1L, journalId = journalId, title = "Entry 1", content = "Content", date = 1000L, createdAt = 10L)
        val e2 = JournalEntry(id = 2L, journalId = journalId, title = "Entry 2", content = "Content", date = 2000L, createdAt = 20L)
        val other = JournalEntry(id = 3L, journalId = 2L, title = "Other", content = "Content", date = 3000L, createdAt = 30L)
        journalEntryDao.insertEntry(e1)
        journalEntryDao.insertEntry(e2)
        journalEntryDao.insertEntry(other)

        val result = repository.getEntriesByJournalId(journalId).first()

        assertEquals(listOf(e2, e1), result)
        assertEquals(1, journalEntryDao.getEntriesByJournalIdCalls)
    }

    @Test
    fun `test insertEntry calls dao and returns id`() = runTest {
        val entry = JournalEntry(journalId = 1L, title = "New Entry", content = "Content", date = 1000L)

        val result = repository.insertEntry(entry)

        assertEquals(1L, result)
        assertEquals(1, journalEntryDao.insertEntryCalls)
        assertTrue(journalEntryDao.snapshot().any { it.id == 1L && it.title == "New Entry" })
    }

    @Test
    fun `test updateEntry calls dao`() = runTest {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Updated Entry", content = "Content", date = 1000L)
        
        repository.updateEntry(entry)

        assertEquals(1, journalEntryDao.updateEntryCalls)
    }

    @Test
    fun `test deleteEntry calls dao`() = runTest {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Entry to delete", content = "Content", date = 1000L)
        
        repository.deleteEntry(entry)

        assertEquals(1, journalEntryDao.deleteEntryCalls)
    }

    @Test
    fun `test deleteEntryById calls dao`() = runTest {
        repository.deleteEntryById(1L)

        assertEquals(1, journalEntryDao.deleteEntryByIdCalls)
    }
}

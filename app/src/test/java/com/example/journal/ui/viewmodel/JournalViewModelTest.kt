package com.example.journal.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.journal.data.model.Journal
import com.example.journal.data.model.JournalEntry
import com.example.journal.data.repository.JournalRepository
import com.example.journal.testutil.FakeJournalDao
import com.example.journal.testutil.FakeJournalEntryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var journalDao: FakeJournalDao
    private lateinit var journalEntryDao: FakeJournalEntryDao
    private lateinit var repository: JournalRepository

    private lateinit var viewModel: JournalViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        journalDao = FakeJournalDao()
        journalEntryDao = FakeJournalEntryDao()
        repository = JournalRepository(journalDao, journalEntryDao)
        viewModel = JournalViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test allJournals flow delegates to repository`() = runTest(testDispatcher) {
        val j1 = Journal(id = 1L, name = "Journal 1", createdAt = 100L, isDeleted = false)
        val j2 = Journal(id = 2L, name = "Journal 2", createdAt = 200L, isDeleted = false)
        journalDao.insertJournal(j1)
        journalDao.insertJournal(j2)
        val result = viewModel.allJournals.first()

        assertEquals(listOf(j2, j1), result)
    }

    @Test
    fun `test allEntries flow delegates to repository`() = runTest(testDispatcher) {
        val e1 = JournalEntry(id = 1L, journalId = 1L, title = "Entry 1", content = "Content", date = 1000L, createdAt = 10L)
        val e2 = JournalEntry(id = 2L, journalId = 1L, title = "Entry 2", content = "Content", date = 2000L, createdAt = 20L)
        journalEntryDao.insertEntry(e1)
        journalEntryDao.insertEntry(e2)
        val result = viewModel.allEntries.first()

        assertEquals(listOf(e2, e1), result)
    }

    @Test
    fun `test getEntryById sets selected entry`() = runTest(testDispatcher) {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Test Entry", content = "Content", date = 1000L)
        journalEntryDao.insertEntry(entry)

        viewModel.getEntryById(1L)
        advanceUntilIdle()

        assertEquals(entry, viewModel.selectedEntry.value)
    }

    @Test
    fun `test getEntryById sets null when entry not found`() = runTest(testDispatcher) {
        viewModel.getEntryById(999L)
        advanceUntilIdle()

        assertNull(viewModel.selectedEntry.value)
    }

    @Test
    fun `test getEntriesByDate delegates to repository`() = runTest(testDispatcher) {
        val startOfDay = 1000L
        val endOfDay = 2000L
        val e1 = JournalEntry(id = 1L, journalId = 1L, title = "Entry", content = "Content", date = 1500L, createdAt = 10L)
        val e2 = JournalEntry(id = 2L, journalId = 1L, title = "Out of range", content = "Content", date = 2500L, createdAt = 20L)
        journalEntryDao.insertEntry(e1)
        journalEntryDao.insertEntry(e2)

        val result = viewModel.getEntriesByDate(startOfDay, endOfDay).first()

        assertEquals(listOf(e1), result)
    }

    @Test
    fun `test insertEntry sets success state on success`() = runTest(testDispatcher) {
        val entry = JournalEntry(journalId = 1L, title = "New Entry", content = "Content", date = 1000L)

        viewModel.insertEntry(entry)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is JournalUiState.Success)
        assertEquals(1, journalEntryDao.snapshot().size)
    }

    @Test
    fun `test insertEntry sets error state on exception`() = runTest(testDispatcher) {
        val entry = JournalEntry(journalId = 1L, title = "New Entry", content = "Content", date = 1000L)
        val errorMessage = "Database error"
        journalEntryDao.throwOnInsert = RuntimeException(errorMessage)

        viewModel.insertEntry(entry)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is JournalUiState.Error)
        assertEquals(errorMessage, (state as JournalUiState.Error).message)
    }

    @Test
    fun `test updateEntry sets success state and updates timestamp`() = runTest(testDispatcher) {
        val entry = JournalEntry(
            id = 1L,
            journalId = 1L,
            title = "Updated Entry",
            content = "Content",
            date = 1000L,
            createdAt = 0L,
            updatedAt = 0L
        )
        journalEntryDao.insertEntry(entry)
        
        viewModel.updateEntry(entry)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is JournalUiState.Success)
        val updated = journalEntryDao.snapshot().first { it.id == 1L }
        assertTrue(updated.updatedAt > entry.updatedAt)
    }

    @Test
    fun `test updateEntry sets error state on exception`() = runTest(testDispatcher) {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Entry", content = "Content", date = 1000L)
        val errorMessage = "Update failed"
        journalEntryDao.throwOnUpdate = RuntimeException(errorMessage)

        viewModel.updateEntry(entry)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is JournalUiState.Error)
        assertEquals(errorMessage, (state as JournalUiState.Error).message)
    }

    @Test
    fun `test deleteEntry sets success state on success`() = runTest(testDispatcher) {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Entry to delete", content = "Content", date = 1000L)
        journalEntryDao.insertEntry(entry)
        
        viewModel.deleteEntry(entry)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is JournalUiState.Success)
        assertTrue(journalEntryDao.snapshot().isEmpty())
    }

    @Test
    fun `test deleteEntry sets error state on exception`() = runTest(testDispatcher) {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Entry", content = "Content", date = 1000L)
        val errorMessage = "Delete failed"
        journalEntryDao.throwOnDelete = RuntimeException(errorMessage)

        viewModel.deleteEntry(entry)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is JournalUiState.Error)
        assertEquals(errorMessage, (state as JournalUiState.Error).message)
    }

    @Test
    fun `test deleteEntryById sets success state on success`() = runTest(testDispatcher) {
        val entry = JournalEntry(id = 1L, journalId = 1L, title = "Entry", content = "Content", date = 1000L)
        journalEntryDao.insertEntry(entry)

        viewModel.deleteEntryById(1L)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is JournalUiState.Success)
        assertTrue(journalEntryDao.snapshot().isEmpty())
    }

    @Test
    fun `test clearSelectedEntry sets selected entry to null`() {
        viewModel.clearSelectedEntry()

        assertNull(viewModel.selectedEntry.value)
    }

    @Test
    fun `test clearUiState sets state to idle`() {
        viewModel.clearUiState()

        assertTrue(viewModel.uiState.value is JournalUiState.Idle)
    }

    @Test
    fun `test getEntriesByJournalId delegates to repository`() = runTest(testDispatcher) {
        val journalId = 1L
        val e1 = JournalEntry(id = 1L, journalId = journalId, title = "Entry 1", content = "Content", date = 1000L, createdAt = 10L)
        val e2 = JournalEntry(id = 2L, journalId = journalId, title = "Entry 2", content = "Content", date = 2000L, createdAt = 20L)
        val other = JournalEntry(id = 3L, journalId = 2L, title = "Other", content = "Content", date = 3000L, createdAt = 30L)
        journalEntryDao.insertEntry(e1)
        journalEntryDao.insertEntry(e2)
        journalEntryDao.insertEntry(other)

        val result = viewModel.getEntriesByJournalId(journalId).first()

        assertEquals(listOf(e2, e1), result)
    }

    @Test
    fun `test setSelectedJournal updates selected journal id`() = runTest(testDispatcher) {
        viewModel.setSelectedJournal(1L)
        advanceUntilIdle()

        assertEquals(1L, viewModel.selectedJournalId.value)
    }

    @Test
    fun `test setSelectedJournal with null clears selection`() = runTest(testDispatcher) {
        viewModel.setSelectedJournal(null)
        advanceUntilIdle()

        assertNull(viewModel.selectedJournalId.value)
    }

    @Test
    fun `test insertJournal delegates to repository`() = runTest(testDispatcher) {
        val journal = Journal(name = "New Journal")

        val result = viewModel.insertJournal(journal)

        assertEquals(1L, result)
        assertTrue(journalDao.snapshot().any { it.id == 1L && it.name == "New Journal" })
    }

    @Test
    fun `test deleteJournal delegates to repository`() = runTest(testDispatcher) {
        val journal = Journal(id = 1L, name = "Journal to delete")
        journalDao.insertJournal(journal)
        
        viewModel.deleteJournal(journal)

        assertTrue(journalDao.snapshot().none { it.id == 1L })
    }

    @Test
    fun `test deleteJournalById delegates to repository`() = runTest(testDispatcher) {
        val journal = Journal(id = 1L, name = "Journal to delete")
        journalDao.insertJournal(journal)

        viewModel.deleteJournalById(1L)

        assertTrue(journalDao.snapshot().none { it.id == 1L })
    }

    @Test
    fun `test updateJournal delegates to repository`() = runTest(testDispatcher) {
        val existing = Journal(id = 1L, name = "Old Journal", createdAt = 100L)
        journalDao.insertJournal(existing)
        val journal = existing.copy(name = "Updated Journal")
        
        viewModel.updateJournal(journal)

        assertEquals("Updated Journal", journalDao.snapshot().first { it.id == 1L }.name)
    }
}

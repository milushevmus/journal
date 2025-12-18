package com.example.journal.ui.viewmodel

import com.example.journal.data.model.JournalEntry
import com.example.journal.data.repository.JournalRepository
import com.example.journal.testutil.FakeJournalDao
import com.example.journal.testutil.FakeJournalEntryDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateViewModelTest {

    private fun makeRepoWithEntries(entries: List<JournalEntry>): Pair<JournalRepository, FakeJournalEntryDao> {
        val journalDao = FakeJournalDao()
        val entryDao = FakeJournalEntryDao(entries)
        return JournalRepository(journalDao, entryDao) to entryDao
    }

    @Test
    fun `setSelectedDate updates selectedDate StateFlow`() = runTest {
        val (repo, _) = makeRepoWithEntries(emptyList())
        val vm = DateViewModel(repo)

        val newTs = 1_700_000_000_000L
        vm.setSelectedDate(newTs)

        assertEquals(newTs, vm.selectedDate.value)
    }

    @Test
    fun `getStartOfDay sets time fields to 00_00_00_000`() = runTest {
        val (repo, _) = makeRepoWithEntries(emptyList())
        val vm = DateViewModel(repo)

        val timestamp = 1_700_000_123_456L
        val start = vm.getStartOfDay(timestamp)

        val cal = Calendar.getInstance().apply { timeInMillis = start }
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
        assertEquals(0, cal.get(Calendar.SECOND))
        assertEquals(0, cal.get(Calendar.MILLISECOND))
    }

    @Test
    fun `getEndOfDay sets time fields to 23_59_59_999`() = runTest {
        val (repo, _) = makeRepoWithEntries(emptyList())
        val vm = DateViewModel(repo)

        val timestamp = 1_700_000_123_456L
        val end = vm.getEndOfDay(timestamp)

        val cal = Calendar.getInstance().apply { timeInMillis = end }
        assertEquals(23, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, cal.get(Calendar.MINUTE))
        assertEquals(59, cal.get(Calendar.SECOND))
        assertEquals(999, cal.get(Calendar.MILLISECOND))
    }

    @Test
    fun `getEntriesForSelectedDate delegates to repository with start and end of selected day`() = runTest {
        val selected = 1_700_000_123_456L

        val (repo, entryDao) = makeRepoWithEntries(
            listOf(
                JournalEntry(
                    id = 1L,
                    journalId = 1L,
                    title = "in-range",
                    content = "c",
                    date = selected,
                    createdAt = selected,
                    updatedAt = selected
                )
            )
        )

        val vm = DateViewModel(repo)
        vm.setSelectedDate(selected)

        assertNull(entryDao.lastGetEntriesByDateStartOfDay)
        assertNull(entryDao.lastGetEntriesByDateEndOfDay)

        val list = vm.getEntriesForSelectedDate().first()

        val start = entryDao.lastGetEntriesByDateStartOfDay
        val end = entryDao.lastGetEntriesByDateEndOfDay
        assertNotNull(start)
        assertNotNull(end)
        assertTrue("start should be <= end", start!! <= end!!)

        // sanity: returned list comes from DAO filtering
        assertEquals(1, list.size)
        assertEquals("in-range", list.first().title)
    }

    @Test
    fun `DateViewModelFactory creates DateViewModel`() {
        val (repo, _) = makeRepoWithEntries(emptyList())
        val factory = DateViewModelFactory(repo)

        val vm = factory.create(DateViewModel::class.java)
        assertTrue(vm is DateViewModel)
    }
}




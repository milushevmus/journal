package com.example.journal.testutil

import com.example.journal.data.database.JournalEntryDao
import com.example.journal.data.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeJournalEntryDao(
    initial: List<JournalEntry> = emptyList()
) : JournalEntryDao {

    private val backing = MutableStateFlow(initial)
    private var nextId: Long = (initial.maxOfOrNull { it.id } ?: 0L) + 1L

    var getAllEntriesCalls = 0
    var getEntryByIdCalls = 0
    var getEntriesByDateCalls = 0
    var getEntriesByJournalIdCalls = 0
    var insertEntryCalls = 0
    var updateEntryCalls = 0
    var deleteEntryCalls = 0
    var deleteEntryByIdCalls = 0

    var throwOnGetById: Throwable? = null
    var throwOnInsert: Throwable? = null
    var throwOnUpdate: Throwable? = null
    var throwOnDelete: Throwable? = null
    var throwOnDeleteById: Throwable? = null

    override fun getAllEntries(): Flow<List<JournalEntry>> {
        getAllEntriesCalls++
        return backing.map { list ->
            list.sortedWith(compareByDescending<JournalEntry> { it.date }.thenByDescending { it.createdAt })
        }
    }

    override suspend fun getEntryById(id: Long): JournalEntry? {
        getEntryByIdCalls++
        throwOnGetById?.let { throw it }
        return backing.value.firstOrNull { it.id == id }
    }

    override fun getEntriesByDate(startOfDay: Long, endOfDay: Long): Flow<List<JournalEntry>> {
        getEntriesByDateCalls++
        return backing.map { list ->
            list.filter { it.date >= startOfDay && it.date < endOfDay }
                .sortedByDescending { it.createdAt }
        }
    }

    override fun getEntriesByJournalId(journalId: Long): Flow<List<JournalEntry>> {
        getEntriesByJournalIdCalls++
        return backing.map { list ->
            list.filter { it.journalId == journalId }
                .sortedWith(compareByDescending<JournalEntry> { it.date }.thenByDescending { it.createdAt })
        }
    }

    override suspend fun insertEntry(entry: JournalEntry): Long {
        insertEntryCalls++
        throwOnInsert?.let { throw it }

        val id = if (entry.id != 0L) entry.id else nextId++
        val withId = entry.copy(id = id)

        backing.value = backing.value.filterNot { it.id == id } + withId
        return id
    }

    override suspend fun updateEntry(entry: JournalEntry) {
        updateEntryCalls++
        throwOnUpdate?.let { throw it }

        if (entry.id == 0L) return
        backing.value = backing.value.map { existing ->
            if (existing.id == entry.id) entry else existing
        }
    }

    override suspend fun deleteEntry(entry: JournalEntry) {
        deleteEntryCalls++
        throwOnDelete?.let { throw it }
        backing.value = backing.value.filterNot { it.id == entry.id }
    }

    override suspend fun deleteEntryById(id: Long) {
        deleteEntryByIdCalls++
        throwOnDeleteById?.let { throw it }
        backing.value = backing.value.filterNot { it.id == id }
    }

    fun snapshot(): List<JournalEntry> = backing.value
}



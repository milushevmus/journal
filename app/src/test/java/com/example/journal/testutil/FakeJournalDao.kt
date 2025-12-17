package com.example.journal.testutil

import com.example.journal.data.database.JournalDao
import com.example.journal.data.model.Journal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeJournalDao(
    initial: List<Journal> = emptyList()
) : JournalDao {

    private val backing = MutableStateFlow(initial)
    private var nextId: Long = (initial.maxOfOrNull { it.id } ?: 0L) + 1L

    var getAllJournalsCalls = 0
    var getJournalByIdCalls = 0
    var getDeletedJournalsCalls = 0
    var insertJournalCalls = 0
    var updateJournalCalls = 0
    var deleteJournalCalls = 0
    var deleteJournalByIdCalls = 0

    var throwOnGetById: Throwable? = null
    var throwOnInsert: Throwable? = null
    var throwOnUpdate: Throwable? = null
    var throwOnDelete: Throwable? = null
    var throwOnDeleteById: Throwable? = null

    override fun getAllJournals(): Flow<List<Journal>> {
        getAllJournalsCalls++
        return backing.map { list ->
            list.filter { !it.isDeleted }.sortedByDescending { it.createdAt }
        }
    }

    override suspend fun getJournalById(id: Long): Journal? {
        getJournalByIdCalls++
        throwOnGetById?.let { throw it }
        return backing.value.firstOrNull { it.id == id }
    }

    override fun getDeletedJournals(): Flow<List<Journal>> {
        getDeletedJournalsCalls++
        return backing.map { list ->
            list.filter { it.isDeleted }.sortedByDescending { it.createdAt }
        }
    }

    override suspend fun insertJournal(journal: Journal): Long {
        insertJournalCalls++
        throwOnInsert?.let { throw it }

        val id = if (journal.id != 0L) journal.id else nextId++
        val withId = journal.copy(id = id)

        backing.value = backing.value.filterNot { it.id == id } + withId
        return id
    }

    override suspend fun updateJournal(journal: Journal) {
        updateJournalCalls++
        throwOnUpdate?.let { throw it }

        if (journal.id == 0L) return
        backing.value = backing.value.map { existing ->
            if (existing.id == journal.id) journal else existing
        }
    }

    override suspend fun deleteJournal(journal: Journal) {
        deleteJournalCalls++
        throwOnDelete?.let { throw it }
        backing.value = backing.value.filterNot { it.id == journal.id }
    }

    override suspend fun deleteJournalById(id: Long) {
        deleteJournalByIdCalls++
        throwOnDeleteById?.let { throw it }
        backing.value = backing.value.filterNot { it.id == id }
    }

    fun snapshot(): List<Journal> = backing.value
}



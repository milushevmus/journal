package com.example.journal.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.journal.data.model.Journal
import com.example.journal.data.model.JournalEntry
import com.example.journal.data.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JournalViewModel(private val repository: JournalRepository) : ViewModel() {
    
    val allJournals: Flow<List<Journal>> = repository.getAllJournals()
    val allEntries: Flow<List<JournalEntry>> = repository.getAllEntries()
    
    private val _selectedJournalId = MutableStateFlow<Long?>(null)
    val selectedJournalId: StateFlow<Long?> = _selectedJournalId.asStateFlow()
    
    private val _selectedEntry = MutableStateFlow<JournalEntry?>(null)
    val selectedEntry: StateFlow<JournalEntry?> = _selectedEntry.asStateFlow()
    
    private val _uiState = MutableStateFlow<JournalUiState>(JournalUiState.Idle)
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()
    
    fun getEntryById(id: Long) {
        viewModelScope.launch {
            _selectedEntry.value = repository.getEntryById(id)
        }
    }
    
    fun getEntriesByDate(startOfDay: Long, endOfDay: Long): Flow<List<JournalEntry>> {
        return repository.getEntriesByDate(startOfDay, endOfDay)
    }
    
    fun insertEntry(entry: JournalEntry) {
        viewModelScope.launch {
            try {
                repository.insertEntry(entry)
                _uiState.value = JournalUiState.Success
            } catch (e: Exception) {
                _uiState.value = JournalUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun updateEntry(entry: JournalEntry) {
        viewModelScope.launch {
            try {
                val updatedEntry = entry.copy(updatedAt = System.currentTimeMillis())
                repository.updateEntry(updatedEntry)
                _uiState.value = JournalUiState.Success
            } catch (e: Exception) {
                _uiState.value = JournalUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            try {
                repository.deleteEntry(entry)
                _uiState.value = JournalUiState.Success
            } catch (e: Exception) {
                _uiState.value = JournalUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun deleteEntryById(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteEntryById(id)
                _uiState.value = JournalUiState.Success
            } catch (e: Exception) {
                _uiState.value = JournalUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun clearSelectedEntry() {
        _selectedEntry.value = null
    }
    
    fun clearUiState() {
        _uiState.value = JournalUiState.Idle
    }
    
    // Journal operations
    fun getEntriesByJournalId(journalId: Long): Flow<List<JournalEntry>> {
        return repository.getEntriesByJournalId(journalId)
    }
    
    fun setSelectedJournal(journalId: Long?) {
        viewModelScope.launch {
            _selectedJournalId.value = journalId
        }
    }
    
    suspend fun insertJournal(journal: Journal): Long {
        return repository.insertJournal(journal)
    }
    
    suspend fun deleteJournal(journal: Journal) {
        repository.deleteJournal(journal)
    }
    
    suspend fun deleteJournalById(id: Long) {
        repository.deleteJournalById(id)
    }
    
    suspend fun updateJournal(journal: Journal) {
        repository.updateJournal(journal)
    }
}

sealed class JournalUiState {
    object Idle : JournalUiState()
    object Success : JournalUiState()
    data class Error(val message: String) : JournalUiState()
}

class JournalViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

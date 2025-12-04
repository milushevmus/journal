package com.example.journal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journal.data.model.JournalEntry
import com.example.journal.data.preferences.JournalPreferences
import com.example.journal.ui.components.EmptyState
import com.example.journal.ui.components.EntryCard
import com.example.journal.ui.viewmodels.JournalViewModel
import com.example.journal.ui.viewmodel.getJournalViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalHomeScreen(
    selectedJournalId: Long?,
    onNavigateToEntriesList: () -> Unit,
    onNavigateToEntryDetail: (Long) -> Unit,
    onNavigateToEntryEdit: (Long?) -> Unit,
    onJournalSelected: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: JournalViewModel = viewModel(
        factory = getJournalViewModelFactory(context)
    )
    val scope = rememberCoroutineScope()
    val preferences = remember { JournalPreferences(context) }
    
    // Get selected journal name
    val journals by viewModel.allJournals.collectAsState(initial = emptyList())
    val selectedJournal = journals.find { it.id == selectedJournalId }
    val journalName = selectedJournal?.name ?: "Select a Journal"
    
    // Get entries for selected journal
    val entries = if (selectedJournalId != null) {
        val entriesFlow = viewModel.getEntriesByJournalId(selectedJournalId)
        val entriesState by entriesFlow.collectAsState(initial = emptyList())
        entriesState
    } else {
        emptyList()
    }
    
    LaunchedEffect(journals, selectedJournalId) {
        if (selectedJournalId == null && journals.isNotEmpty()) {
            val firstJournalId = journals.first().id
            viewModel.setSelectedJournal(firstJournalId)
            preferences.setSelectedJournalId(firstJournalId)
            onJournalSelected?.invoke(firstJournalId)
        } else if (selectedJournalId != null) {
            viewModel.setSelectedJournal(selectedJournalId)
        }
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(journalName)
                        IconButton(onClick = onNavigateToEntriesList) {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Select Journal"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToEntryEdit(null) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Entry",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        if (selectedJournalId == null) {
            EmptyState(
                message = "No Journal Selected",
                description = "Select or create a journal to view entries"
            )
        } else if (entries.isEmpty()) {
            EmptyState(
                message = "No Entries",
                description = "Start journaling by creating your first entry"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries) { entry ->
                    EntryCard(
                        entry = entry,
                        onClick = { onNavigateToEntryDetail(entry.id) },
                        onDelete = {
                            scope.launch {
                                viewModel.deleteEntryById(entry.id)
                            }
                        },
                        onShare = { /* TODO: Share entry */ }
                    )
                }
            }
        }
    }
}

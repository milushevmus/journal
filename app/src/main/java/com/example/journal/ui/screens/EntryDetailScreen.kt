package com.example.journal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journal.data.model.JournalEntry
import com.example.journal.ui.viewmodels.JournalViewModel
import com.example.journal.ui.viewmodel.getJournalViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    entryId: Long?,
    journalId: Long?,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: JournalViewModel = viewModel(
        factory = getJournalViewModelFactory(context)
    )
    
    var isEditMode by remember { mutableStateOf(entryId == null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    
    val selectedEntry by viewModel.selectedEntry.collectAsState()
    
    // Load entry if editing existing
    LaunchedEffect(entryId) {
        if (entryId != null && !isEditMode) {
            viewModel.getEntryById(entryId)
        }
    }
    
    // Update local state when entry is loaded
    LaunchedEffect(selectedEntry) {
        if (selectedEntry != null && !isEditMode) {
            title = selectedEntry!!.title
            content = selectedEntry!!.content
        }
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Entry" else "Entry Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isEditMode && entryId != null) {
                        IconButton(onClick = { isEditMode = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = {
                            viewModel.deleteEntryById(entryId)
                            onNavigateBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (isEditMode) {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { /* TODO: Share */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(onClick = { /* TODO: Undo */ }) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo")
                        }
                        IconButton(onClick = { /* TODO: Redo */ }) {
                            Icon(Icons.Default.Redo, contentDescription = "Redo")
                        }
                        IconButton(onClick = { /* TODO: Text formatting */ }) {
                            Icon(Icons.Default.FormatBold, contentDescription = "Format")
                        }
                        IconButton(onClick = { /* TODO: Image */ }) {
                            Icon(Icons.Default.Image, contentDescription = "Image")
                        }
                        IconButton(onClick = { /* TODO: More options */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        IconButton(
                            onClick = {
                                if (entryId != null) {
                                    // Update existing entry
                                    val updatedEntry = selectedEntry!!.copy(
                                        title = title,
                                        content = content,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    viewModel.updateEntry(updatedEntry)
                                } else {
                                    // Create new entry
                                    val newEntry = JournalEntry(
                                        journalId = journalId ?: 0L,
                                        title = title,
                                        content = content,
                                        date = System.currentTimeMillis()
                                    )
                                    viewModel.insertEntry(newEntry)
                                }
                                isEditMode = false
                                onNavigateBack()
                            }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEditMode) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    maxLines = Int.MAX_VALUE,
                    textStyle = TextStyle.Default
                )
            } else {
                // View mode
                if (selectedEntry != null) {
                    Text(
                        text = selectedEntry!!.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = formatDate(selectedEntry!!.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedEntry!!.content,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else if (entryId == null) {
                    // New entry - show empty fields
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp),
                        maxLines = Int.MAX_VALUE
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
        .format(Date(timestamp))
}

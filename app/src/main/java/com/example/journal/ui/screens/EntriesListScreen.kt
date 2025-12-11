package com.example.journal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journal.data.model.Journal
import com.example.journal.ui.components.EmptyState
import com.example.journal.ui.components.JournalEditBottomSheet
import com.example.journal.ui.viewmodel.JournalViewModel
import com.example.journal.ui.viewmodel.getJournalViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriesListScreen(
    onNavigateBack: () -> Unit,
    onJournalSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: JournalViewModel = viewModel(
        factory = getJournalViewModelFactory(context)
    )
    val journals by viewModel.allJournals.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    var showBottomSheet by remember { mutableStateOf(false) }
    var journalToEdit by remember { mutableStateOf<Journal?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Journal?>(null) }
    var showMenuForJournal by remember { mutableStateOf<Journal?>(null) }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Journals") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    journalToEdit = null
                    showBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Journal",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        if (journals.isEmpty()) {
            EmptyState(
                message = "No Journals",
                description = "Create your first journal to get started"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(journals) { journal ->
                    var showMenu by remember { mutableStateOf(false) }
                    
                    val iconVector = getIconFromString(journal.icon)
                    val journalColor = Color(android.graphics.Color.parseColor(journal.color))
                    
                    ListItem(
                        leadingContent = {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = journalColor
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        iconVector,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        headlineContent = { Text(journal.name) },
                        trailingContent = {
                            Row {
                                Box {
                                    IconButton(
                                        onClick = { showMenu = true }
                                    ) {
                                        Icon(
                                            Icons.Default.MoreVert,
                                            contentDescription = "More options"
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Edit") },
                                            onClick = {
                                                showMenu = false
                                                journalToEdit = journal
                                                showBottomSheet = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { 
                                                Text(
                                                    "Delete",
                                                    color = MaterialTheme.colorScheme.error
                                                ) 
                                            },
                                            onClick = {
                                                showMenu = false
                                                showDeleteDialog = journal
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onJournalSelected(journal.id) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { journal ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Journal?") },
            text = { 
                Text("Are you sure you want to delete \"${journal.name}\"? This will permanently delete the journal and all notes inside it. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.deleteJournalById(journal.id)
                            showDeleteDialog = null
                        }
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Journal edit bottom sheet
    if (showBottomSheet) {
        JournalEditBottomSheet(
            journal = journalToEdit,
            onDismiss = { showBottomSheet = false },
            onSave = { name, color, icon ->
                scope.launch {
                    if (journalToEdit != null) {
                        // Update existing journal
                        val updated = journalToEdit!!.copy(
                            name = name,
                            color = color,
                            icon = icon
                        )
                        viewModel.updateJournal(updated)
                    } else {
                        // Create new journal
                        val newJournal = Journal(
                            name = name,
                            color = color,
                            icon = icon
                        )
                        val journalId = viewModel.insertJournal(newJournal)
                        onJournalSelected(journalId)
                    }
                }
            }
        )
    }
}

private fun getIconFromString(iconString: String): ImageVector {
    return when (iconString) {
        "smile" -> Icons.Default.SentimentSatisfied
        "description" -> Icons.Default.Description
        "home" -> Icons.Default.Home
        "bed" -> Icons.Default.Bedtime
        "restaurant" -> Icons.Default.Restaurant
        "book" -> Icons.Default.Book
        "phone" -> Icons.Default.Phone
        "key" -> Icons.Default.Key
        "lightbulb" -> Icons.Default.Lightbulb
        "camera" -> Icons.Default.CameraAlt
        "favorite" -> Icons.Default.Favorite
        "star" -> Icons.Default.Star
        "work" -> Icons.Default.Work
        "school" -> Icons.Default.School
        "sports" -> Icons.Default.SportsSoccer
        "music" -> Icons.Default.MusicNote
        "travel" -> Icons.Default.Flight
        "shopping" -> Icons.Default.ShoppingCart
        "food" -> Icons.Default.RestaurantMenu
        "health" -> Icons.Default.LocalHospital
        else -> Icons.Default.Description
    }
}

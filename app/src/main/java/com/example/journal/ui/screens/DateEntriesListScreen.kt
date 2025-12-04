package com.example.journal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journal.ui.components.EmptyState
import com.example.journal.ui.components.EntryCard
import com.example.journal.ui.viewmodels.JournalViewModel
import com.example.journal.ui.viewmodel.getJournalViewModelFactory
import com.example.journal.navigation.JournalRoutes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEntriesListScreen(
    selectedDate: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEntryDetail: (Long) -> Unit,
    onNavigateToEntryEdit: () -> Unit,
    onDateChanged: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: JournalViewModel = viewModel(
        factory = getJournalViewModelFactory(context)
    )
    val scope = rememberCoroutineScope()
    
    var currentDate by remember { mutableStateOf(selectedDate) }
    
    // Calculate start and end of day
    val calendar = Calendar.getInstance().apply { timeInMillis = currentDate }
    val startOfDay = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    
    val endOfDay = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
    
    val entries by viewModel.getEntriesByDate(startOfDay, endOfDay)
        .collectAsState(initial = emptyList())
    
    val dateFormatter = remember { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) }
    val dateString = dateFormatter.format(Date(currentDate))
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(dateString) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FloatingActionButton(
                        onClick = onNavigateToEntryEdit,
                        modifier = Modifier.size(40.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Entry",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (entries.isEmpty()) {
                EmptyState(
                    message = "No Entries",
                    description = "This day currently has no journal entries.",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
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
            
            // Day navigation arrows
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val prevDay = Calendar.getInstance().apply {
                            timeInMillis = currentDate
                            add(Calendar.DAY_OF_MONTH, -1)
                        }.timeInMillis
                        currentDate = prevDay
                        onDateChanged(prevDay)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous day"
                    )
                }
                IconButton(
                    onClick = {
                        val nextDay = Calendar.getInstance().apply {
                            timeInMillis = currentDate
                            add(Calendar.DAY_OF_MONTH, 1)
                        }.timeInMillis
                        currentDate = nextDay
                        onDateChanged(nextDay)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next day"
                    )
                }
            }
        }
    }
}

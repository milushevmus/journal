package com.example.journal.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.journal.data.model.JournalEntry
import com.example.journal.ui.viewmodel.JournalViewModel
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
    var imageUri by remember { mutableStateOf<String?>(null) }
    
    data class TextState(val title: String, val content: String)
    val undoHistory = remember { mutableStateListOf<TextState>() }
    val redoHistory = remember { mutableStateListOf<TextState>() }
    var currentHistoryIndex by remember { mutableIntStateOf(-1) }
    
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isUnderline by remember { mutableStateOf(false) }
    
    var showMoreMenu by remember { mutableStateOf(false) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it.toString()
        }
    }
    
    val selectedEntry by viewModel.selectedEntry.collectAsState()
    
    LaunchedEffect(entryId) {
        if (entryId != null && !isEditMode) {
            viewModel.getEntryById(entryId)
        }
    }
    
    LaunchedEffect(selectedEntry) {
        if (selectedEntry != null && !isEditMode) {
            title = selectedEntry!!.title
            content = selectedEntry!!.content
            imageUri = selectedEntry!!.imageUri
            undoHistory.clear()
            redoHistory.clear()
            undoHistory.add(TextState(title, content))
            currentHistoryIndex = 0
        }
    }
    
    fun saveToHistory() {
        val newState = TextState(title, content)
        if (undoHistory.isEmpty() || undoHistory.last() != newState) {
            undoHistory.add(newState)
            redoHistory.clear()
            currentHistoryIndex = undoHistory.size - 1
            if (undoHistory.size > 50) {
                undoHistory.removeAt(0)
                currentHistoryIndex--
            }
        }
    }
    
    fun undo() {
        if (currentHistoryIndex > 0) {
            val currentState = TextState(title, content)
            redoHistory.add(currentState)
            currentHistoryIndex--
            val previousState = undoHistory[currentHistoryIndex]
            title = previousState.title
            content = previousState.content
        }
    }
    
    fun redo() {
        if (redoHistory.isNotEmpty()) {
            val currentState = TextState(title, content)
            undoHistory.add(currentState)
            currentHistoryIndex++
            val nextState = redoHistory.removeLast()
            title = nextState.title
            content = nextState.content
        }
    }
    
    fun shareEntry() {
        val entryTitle = if (isEditMode) title else (selectedEntry?.title ?: title)
        val entryContent = if (isEditMode) content else (selectedEntry?.content ?: content)
        val shareText = "$entryTitle\n\n$entryContent"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
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
                        IconButton(
                            onClick = { 
                                shareEntry()
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(
                            onClick = { undo() },
                            enabled = currentHistoryIndex > 0
                        ) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo")
                        }
                        IconButton(
                            onClick = { redo() },
                            enabled = redoHistory.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Redo, contentDescription = "Redo")
                        }
                        Box {
                            IconButton(
                                onClick = { 
                                    isBold = !isBold
                                }
                            ) {
                                Icon(
                                    Icons.Default.FormatBold, 
                                    contentDescription = "Format",
                                    tint = if (isBold) MaterialTheme.colorScheme.primary 
                                           else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        IconButton(
                            onClick = { 
                                imagePickerLauncher.launch("image/*")
                            }
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "Image")
                        }
                        Box {
                            IconButton(
                                onClick = { showMoreMenu = true }
                            ) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Italic") },
                                    onClick = {
                                        isItalic = !isItalic
                                        showMoreMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.FormatItalic,
                                            contentDescription = null,
                                            tint = if (isItalic) MaterialTheme.colorScheme.primary 
                                                   else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Underline") },
                                    onClick = {
                                        isUnderline = !isUnderline
                                        showMoreMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.FormatUnderlined,
                                            contentDescription = null,
                                            tint = if (isUnderline) MaterialTheme.colorScheme.primary 
                                                   else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                if (entryId != null) {
                                    // Update existing entry
                                    val updatedEntry = selectedEntry!!.copy(
                                        title = title,
                                        content = content,
                                        imageUri = imageUri,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    viewModel.updateEntry(updatedEntry)
                                } else {
                                    // Create new entry
                                    val newEntry = JournalEntry(
                                        journalId = journalId ?: 0L,
                                        title = title,
                                        content = content,
                                        date = System.currentTimeMillis(),
                                        imageUri = imageUri
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
                    onValueChange = { 
                        title = it
                        saveToHistory()
                    },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { 
                        content = it
                        saveToHistory()
                    },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    maxLines = Int.MAX_VALUE,
                    textStyle = TextStyle(
                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                        textDecoration = if (isUnderline) TextDecoration.Underline else null,
                        fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal
                    )
                )
                
                // Image display and management in edit mode
                imageUri?.let { uriString ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(android.net.Uri.parse(uriString))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Entry image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { imageUri = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.errorContainer
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove image",
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
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
                    
                    // Display image if available
                    selectedEntry!!.imageUri?.let { uriString ->
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(android.net.Uri.parse(uriString))
                                .crossfade(true)
                                .build(),
                            contentDescription = "Entry image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
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

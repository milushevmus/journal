package com.example.journal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.journal.data.model.JournalEntry
import com.example.journal.data.model.MoodState
import com.example.journal.data.preferences.JournalPreferences
import com.example.journal.ui.viewmodel.JournalViewModel
import com.example.journal.ui.viewmodel.getJournalViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDescriptionScreen(
    initialMoodValue: Float = 50f,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: JournalViewModel = viewModel(
        factory = getJournalViewModelFactory(context)
    )
    val scope = rememberCoroutineScope()
    val preferences = remember { JournalPreferences(context) }
    
    // Mood value from 0 to 100
    var moodValue by remember { mutableFloatStateOf(initialMoodValue) }
    
    // Map mood value to animation and label
    val moodState = MoodState.fromMoodValue(moodValue)
    
    // Load Lottie composition
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(moodState.assetPath)
    )
    
    // Text input state
    var textInput by remember { mutableStateOf("") }
    
    // Selected descriptors
    var selectedDescriptors by remember { mutableStateOf(setOf<String>()) }
    
    // Get descriptors for current mood
    val descriptors = moodState.descriptors
    
    // Show more state
    var showMore by remember { mutableStateOf(false) }
    val visibleDescriptors = if (showMore) descriptors else descriptors.take(5)
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        // Navigate to previous mood
                        moodValue = when {
                            moodValue <= 20f -> 50f // Go to Neutral
                            moodValue <= 40f -> 10f // Go to Very Unsatisfied
                            moodValue <= 60f -> 30f // Go to Unsatisfied
                            moodValue <= 80f -> 50f // Go to Neutral
                            else -> 70f // Go to Satisfied
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous mood"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Navigate to next mood
                        moodValue = when {
                            moodValue <= 20f -> 30f // Go to Unsatisfied
                            moodValue <= 40f -> 50f // Go to Neutral
                            moodValue <= 60f -> 70f // Go to Satisfied
                            moodValue <= 80f -> 90f // Go to Very Satisfied
                            else -> 90f // Stay at Very Satisfied
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next mood"
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
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mood Icon
            Box(
                modifier = Modifier
                    .size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                composition?.let {
                    LottieAnimation(
                        composition = it,
                        iterations = Int.MAX_VALUE,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Text(
                    text = moodState.label,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            // Mood Title
            Text(
                text = moodState.label,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Text Input Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "What best describes this feeling?",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Describe your feeling...") },
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            // Predefined Descriptors
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wrap chips in rows - use remember to avoid recomposition issues
                val rows = remember(visibleDescriptors) {
                    val rowList = mutableListOf<List<String>>()
                    var currentRow = mutableListOf<String>()
                    
                    visibleDescriptors.forEach { descriptor: String ->
                        currentRow.add(descriptor)
                        if (currentRow.size >= 3) {
                            rowList.add(currentRow.toList())
                            currentRow.clear()
                        }
                    }
                    if (currentRow.isNotEmpty()) {
                        rowList.add(currentRow)
                    }
                    rowList
                }
                
                rows.forEach { rowDescriptors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowDescriptors.forEach { descriptor ->
                            val isSelected = selectedDescriptors.contains(descriptor)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedDescriptors = if (isSelected) {
                                        selectedDescriptors - descriptor
                                    } else {
                                        selectedDescriptors + descriptor
                                    }
                                },
                                label = { Text(descriptor) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill remaining space if row has less than 3 items
                        repeat(3 - rowDescriptors.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                
                if (descriptors.size > 5) {
                    TextButton(
                        onClick = { showMore = !showMore },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = if (showMore) "Show Less <" else "Show More >",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Save Button
            Button(
                onClick = {
                    val selectedJournalId = preferences.getSelectedJournalId()
                    if (selectedJournalId != null) {
                        // Build content from text input and selected descriptors
                        val contentParts = mutableListOf<String>()
                        if (textInput.isNotBlank()) {
                            contentParts.add(textInput)
                        }
                        if (selectedDescriptors.isNotEmpty()) {
                            contentParts.add(selectedDescriptors.joinToString(", "))
                        }
                        val content = contentParts.joinToString("\n\n")
                        
                        val newEntry = JournalEntry(
                            journalId = selectedJournalId,
                            title = moodState.label,
                            content = content.ifBlank { "No description provided" },
                            date = System.currentTimeMillis(),
                            mood = moodValue.toInt(),
                            imageUri = null // No image for mood entries
                        )
                        
                        scope.launch {
                            viewModel.insertEntry(newEntry)
                            onSave()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = textInput.isNotBlank() || selectedDescriptors.isNotEmpty()
            ) {
                Text("Save Entry", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

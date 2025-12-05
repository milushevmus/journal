package com.example.journal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.journal.ui.viewmodels.JournalViewModel
import com.example.journal.ui.viewmodel.getJournalViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodSelectionScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: JournalViewModel = viewModel(
        factory = getJournalViewModelFactory(context)
    )
    val scope = rememberCoroutineScope()
    
    // Mood value from 0 to 100
    var moodValue by remember { mutableFloatStateOf(50f) }
    
    // Map mood value to animation and label
    val moodState = when {
        moodValue <= 20f -> MoodState.VeryUnsatisfied
        moodValue <= 40f -> MoodState.Unsatisfied
        moodValue <= 60f -> MoodState.Neutral
        moodValue <= 80f -> MoodState.Satisfied
        else -> MoodState.VerySatisfied
    }
    
    // Load Lottie composition
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(moodState.assetPath)
    )
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next"
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Choose how you're feeling right now",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            // Lottie Animation - centered
            Box(
                modifier = Modifier
                    .size(350.dp),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    iterations = Int.MAX_VALUE,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Current Mood Label
            Text(
                text = moodState.label,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 32.dp)
            )
            
            // Mood Slider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "VERY UNPLEASANT",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "VERY PLEASANT",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Slider(
                    value = moodValue,
                    onValueChange = { moodValue = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    }
}

private enum class MoodState(
    val label: String,
    val assetPath: String
) {
    VeryUnsatisfied("Very Unsatisfied", "veryUnsatisfied.lottie"),
    Unsatisfied("Unsatisfied", "unsatisfied.lottie"),
    Neutral("Neutral", "neutral.lottie"),
    Satisfied("Satisfied", "satisfied.lottie"),
    VerySatisfied("Very Satisfied", "verySatisfied.lottie")
}


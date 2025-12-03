package com.example.journal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.journal.data.model.Journal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEditBottomSheet(
    journal: Journal? = null, // null for create, non-null for edit
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit, // name, color, icon
    modifier: Modifier = Modifier
) {
    var journalName by remember { mutableStateOf(journal?.name ?: "") }
    var selectedColor by remember { mutableStateOf(journal?.color ?: "#6650a4") }
    var selectedIcon by remember { mutableStateOf(journal?.icon ?: "description") }
    
    val colors = listOf(
        "#9C27B0", // Purple/Magenta
        "#F44336", // Red/Coral
        "#E91E63", // Light Pink/Lavender
        "#FF9800", // Peach/Light Orange
        "#795548", // Light Brown/Tan
        "#FF5722", // Orange
        "#009688", // Teal/Mint Green
        "#2196F3", // Light Blue
        "#1976D2", // Darker Blue
        "#03A9F4", // Light Gray-Blue
        "#9C27B0", // Lavender/Light Purple
        "#FFFFFF", // White
        "#6650a4" // Default purple
    )
    
    val icons = listOf(
        "smile" to Icons.Default.SentimentSatisfied,
        "description" to Icons.Default.Description,
        "home" to Icons.Default.Home,
        "bed" to Icons.Default.Bedtime,
        "restaurant" to Icons.Default.Restaurant,
        "book" to Icons.Default.Book,
        "phone" to Icons.Default.Phone,
        "key" to Icons.Default.Key,
        "lightbulb" to Icons.Default.Lightbulb,
        "camera" to Icons.Default.CameraAlt,
        "favorite" to Icons.Default.Favorite,
        "star" to Icons.Default.Star,
        "work" to Icons.Default.Work,
        "school" to Icons.Default.School,
        "sports" to Icons.Default.SportsSoccer,
        "music" to Icons.Default.MusicNote,
        "travel" to Icons.Default.Flight,
        "shopping" to Icons.Default.ShoppingCart,
        "food" to Icons.Default.RestaurantMenu,
        "health" to Icons.Default.LocalHospital
    )
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with close and done buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Selected icon preview
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color(android.graphics.Color.parseColor(selectedColor))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val iconVector = icons.find { it.first == selectedIcon }?.second
                            ?: Icons.Default.Description
                        Icon(
                            iconVector,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                IconButton(
                    onClick = {
                        if (journalName.isNotBlank()) {
                            onSave(journalName, selectedColor, selectedIcon)
                            onDismiss()
                        }
                    },
                    enabled = journalName.isNotBlank()
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Done",
                                tint = if (journalName.isNotBlank()) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Journal name input
            OutlinedTextField(
                value = journalName,
                onValueChange = { journalName = it },
                label = { Text("Journal Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Color picker
            Text(
                text = "Color",
                style = MaterialTheme.typography.titleMedium
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(80.dp)
            ) {
                items(colors) { color ->
                    val isSelected = color == selectedColor
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(color)))
                            .clickable { selectedColor = color },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = BorderStroke(
                                    3.dp,
                                    MaterialTheme.colorScheme.onSurface
                                )
                            ) {}
                        }
                    }
                }
            }
            
            // Icon picker
            Text(
                text = "Icon",
                style = MaterialTheme.typography.titleMedium
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(300.dp)
            ) {
                items(icons) { (iconId, iconVector) ->
                    val isSelected = iconId == selectedIcon
                    Surface(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { selectedIcon = iconId },
                        shape = CircleShape,
                        color = if (isSelected) 
                            Color(android.graphics.Color.parseColor(selectedColor))
                        else 
                            MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) 
                            BorderStroke(
                                2.dp,
                                Color(android.graphics.Color.parseColor(selectedColor))
                            )
                        else null
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                iconVector,
                                contentDescription = null,
                                tint = if (isSelected) 
                                    Color.White 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


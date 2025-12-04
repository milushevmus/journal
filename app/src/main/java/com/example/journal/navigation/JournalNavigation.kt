package com.example.journal.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.journal.data.preferences.JournalPreferences
import com.example.journal.ui.screens.DateEntriesListScreen
import com.example.journal.ui.screens.DateSelectionScreen
import com.example.journal.ui.screens.EntriesListScreen
import com.example.journal.ui.screens.EntryDetailScreen
import com.example.journal.ui.screens.JournalHomeScreen

@Composable
fun JournalNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = JournalRoutes.HOME,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val context = LocalContext.current
    val preferences = remember { JournalPreferences(context) }
    
    var selectedJournalId by remember {
        mutableStateOf<Long?>(preferences.getSelectedJournalId())
    }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    
    LaunchedEffect(selectedJournalId) {
        selectedJournalId?.let { preferences.setSelectedJournalId(it) }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(JournalRoutes.HOME) {
            JournalHomeScreen(
                selectedJournalId = selectedJournalId,
                onNavigateToEntriesList = {
                    navController.navigate(JournalRoutes.ENTRIES_LIST)
                },
                onNavigateToEntryDetail = { entryId ->
                    navController.navigate(JournalRoutes.entryDetail(entryId))
                },
                onNavigateToEntryEdit = { entryId ->
                    navController.navigate(JournalRoutes.ENTRY_EDIT)
                },
                onJournalSelected = { journalId ->
                    selectedJournalId = journalId
                    preferences.setSelectedJournalId(journalId)
                }
            )
        }

        composable(JournalRoutes.ENTRIES_LIST) {
            EntriesListScreen(
                onNavigateBack = { navController.popBackStack() },
                onJournalSelected = { journalId ->
                    selectedJournalId = journalId
                    preferences.setSelectedJournalId(journalId)
                    navController.popBackStack()
                }
            )
        }

        composable("${JournalRoutes.ENTRY_DETAIL}/{${JournalRoutes.ENTRY_ID_ARG}}") { backStackEntry ->
            val entryId = backStackEntry.arguments?.getString(JournalRoutes.ENTRY_ID_ARG)?.toLongOrNull()
            EntryDetailScreen(
                entryId = entryId,
                journalId = selectedJournalId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(JournalRoutes.ENTRY_EDIT)
                }
            )
        }

        composable(JournalRoutes.ENTRY_EDIT) {
            EntryDetailScreen(
                entryId = null, // null means create new
                journalId = selectedJournalId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { }
            )
        }

        composable(JournalRoutes.DATE_SELECTION) {
            DateSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onDateSelected = { date ->
                    selectedDate = date
                    navController.navigate(JournalRoutes.dateEntriesList(date))
                }
            )
        }

        composable("${JournalRoutes.DATE_ENTRIES_LIST}/{${JournalRoutes.SELECTED_DATE_ARG}}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString(JournalRoutes.SELECTED_DATE_ARG)?.toLongOrNull()
                ?: System.currentTimeMillis()
            DateEntriesListScreen(
                selectedDate = date,
                onNavigateBack = { 
                    navController.popBackStack(JournalRoutes.DATE_SELECTION, inclusive = false)
                },
                onNavigateToEntryDetail = { entryId ->
                    navController.navigate(JournalRoutes.entryDetail(entryId))
                },
                onNavigateToEntryEdit = {
                    navController.navigate(JournalRoutes.ENTRY_EDIT)
                },
                onDateChanged = { newDate ->
                    selectedDate = newDate
                    navController.navigate(JournalRoutes.dateEntriesList(newDate)) {
                        popUpTo(JournalRoutes.DATE_ENTRIES_LIST) { inclusive = true }
                    }
                }
            )
        }
    }
}

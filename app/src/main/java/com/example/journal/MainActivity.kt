package com.example.journal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.journal.data.database.DatabaseProvider
import com.example.journal.data.repository.JournalRepository
import com.example.journal.navigation.JournalNavigation
import com.example.journal.navigation.JournalRoutes
import com.example.journal.ui.components.JournalBottomNavigationBar
import com.example.journal.ui.theme.JournalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database and repository
        val database = DatabaseProvider.getDatabase(applicationContext)
        val repository = JournalRepository(database.journalDao(), database.journalEntryDao())
        
        setContent {
            JournalTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: JournalRoutes.HOME
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Only show bottom nav on main screens
                        if (currentRoute == JournalRoutes.HOME || 
                            currentRoute == JournalRoutes.MOOD || 
                            currentRoute == JournalRoutes.DATE_SELECTION) {
                            JournalBottomNavigationBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        // Pop up to the start destination to avoid building up a back stack
                                        popUpTo(JournalRoutes.HOME) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    JournalNavigation(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
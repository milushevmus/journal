package com.example.journal.ui.viewmodel

import android.content.Context
import com.example.journal.data.database.DatabaseProvider
import com.example.journal.data.repository.JournalRepository
import com.example.journal.ui.viewmodels.JournalViewModelFactory

fun getJournalRepository(context: Context): JournalRepository {
    val database = DatabaseProvider.getDatabase(context)
    return JournalRepository(database.journalDao(), database.journalEntryDao())
}

fun getJournalViewModelFactory(context: Context): JournalViewModelFactory {
    val repository = getJournalRepository(context)
    return JournalViewModelFactory(repository)
}


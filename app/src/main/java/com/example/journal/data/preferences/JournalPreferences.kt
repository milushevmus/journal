package com.example.journal.data.preferences

import android.content.Context
import android.content.SharedPreferences

class JournalPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    fun getSelectedJournalId(): Long? {
        val id = prefs.getLong(KEY_SELECTED_JOURNAL_ID, -1L)
        return if (id == -1L) null else id
    }
    
    fun setSelectedJournalId(journalId: Long) {
        prefs.edit().putLong(KEY_SELECTED_JOURNAL_ID, journalId).apply()
    }
    
    companion object {
        private const val PREFS_NAME = "journal_preferences"
        private const val KEY_SELECTED_JOURNAL_ID = "selected_journal_id"
    }
}


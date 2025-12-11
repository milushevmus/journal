package com.example.journal.navigation

object JournalRoutes {
    const val HOME = "home"
    const val ENTRIES_LIST = "entries_list"
    const val ENTRY_DETAIL = "entry_detail"
    const val ENTRY_EDIT = "entry_edit"
    const val DATE_SELECTION = "date_selection"
    const val DATE_ENTRIES_LIST = "date_entries_list"
    const val MOOD = "mood"
    const val MOOD_DESCRIPTION = "mood_description"
    
    // Navigation arguments
    const val ENTRY_ID_ARG = "entry_id"
    const val SELECTED_DATE_ARG = "selected_date"
    const val MOOD_VALUE_ARG = "mood_value"
    
    // Route with arguments
    fun entryDetail(entryId: Long) = "$ENTRY_DETAIL/$entryId"
    fun dateEntriesList(date: Long) = "$DATE_ENTRIES_LIST/$date"
    fun moodDescription(moodValue: Float) = "$MOOD_DESCRIPTION/${moodValue.toInt()}"
}


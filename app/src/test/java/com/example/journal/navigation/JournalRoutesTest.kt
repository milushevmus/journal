package com.example.journal.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class JournalRoutesTest {

    @Test
    fun `entryDetail formats route with entry id`() {
        assertEquals("entry_detail/123", JournalRoutes.entryDetail(123L))
    }

    @Test
    fun `dateEntriesList formats route with date`() {
        assertEquals("date_entries_list/1700000123456", JournalRoutes.dateEntriesList(1_700_000_123_456L))
    }

    @Test
    fun `moodDescription casts float mood to int in route`() {
        assertEquals("mood_description/75", JournalRoutes.moodDescription(75.9f))
        assertEquals("mood_description/0", JournalRoutes.moodDescription(0.1f))
    }
}




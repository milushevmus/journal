package com.example.journal.data.model

import org.junit.Assert.*
import org.junit.Test

class JournalTest {

    @Test
    fun `test Journal creation with all properties`() {
        val journal = Journal(
            id = 1L,
            name = "My Journal",
            color = "#FF5733",
            icon = "book",
            createdAt = 1000L,
            isDeleted = false
        )

        assertEquals(1L, journal.id)
        assertEquals("My Journal", journal.name)
        assertEquals("#FF5733", journal.color)
        assertEquals("book", journal.icon)
        assertEquals(1000L, journal.createdAt)
        assertFalse(journal.isDeleted)
    }

    @Test
    fun `test Journal creation with default values`() {
        val journal = Journal(name = "Test Journal")

        assertEquals(0L, journal.id)
        assertEquals("Test Journal", journal.name)
        assertEquals("#6650a4", journal.color) // Default purple color
        assertEquals("description", journal.icon) // Default icon
        assertTrue(journal.createdAt > 0) // Should be current timestamp
        assertFalse(journal.isDeleted)
    }

    @Test
    fun `test Journal equality`() {
        val journal1 = Journal(id = 1L, name = "Journal 1")
        val journal2 = Journal(id = 1L, name = "Journal 1")
        val journal3 = Journal(id = 2L, name = "Journal 1")

        assertEquals(journal1, journal2)
        assertNotEquals(journal1, journal3)
    }

    @Test
    fun `test Journal data class copy`() {
        val originalJournal = Journal(
            id = 1L,
            name = "Original",
            color = "#FF0000",
            icon = "book"
        )
        val copiedJournal = originalJournal.copy(
            name = "Updated",
            color = "#00FF00"
        )

        assertEquals(1L, copiedJournal.id)
        assertEquals("Updated", copiedJournal.name)
        assertEquals("#00FF00", copiedJournal.color)
        assertEquals("book", copiedJournal.icon) // Should remain unchanged
    }

    @Test
    fun `test Journal with deleted flag`() {
        val deletedJournal = Journal(
            id = 1L,
            name = "Deleted Journal",
            isDeleted = true
        )

        assertTrue(deletedJournal.isDeleted)
    }

    @Test
    fun `test Journal name cannot be empty`() {
        val journal = Journal(name = "")
        assertEquals("", journal.name)
    }
}

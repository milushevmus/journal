package com.example.journal.data.model

import org.junit.Assert.*
import org.junit.Test

class JournalEntryTest {

    @Test
    fun `test JournalEntry creation with all properties`() {
        val entry = JournalEntry(
            id = 1L,
            journalId = 10L,
            title = "Test Entry",
            content = "This is test content",
            date = 1000L,
            mood = 75,
            imageUri = "content://image.jpg",
            createdAt = 2000L,
            updatedAt = 3000L
        )

        assertEquals(1L, entry.id)
        assertEquals(10L, entry.journalId)
        assertEquals("Test Entry", entry.title)
        assertEquals("This is test content", entry.content)
        assertEquals(1000L, entry.date)
        assertEquals(75, entry.mood)
        assertEquals("content://image.jpg", entry.imageUri)
        assertEquals(2000L, entry.createdAt)
        assertEquals(3000L, entry.updatedAt)
    }

    @Test
    fun `test JournalEntry creation with default values`() {
        val entry = JournalEntry(
            journalId = 1L,
            title = "Entry",
            content = "Content",
            date = 1000L
        )

        assertEquals(0L, entry.id)
        assertEquals(1L, entry.journalId)
        assertEquals("Entry", entry.title)
        assertEquals("Content", entry.content)
        assertEquals(1000L, entry.date)
        assertNull(entry.mood)
        assertNull(entry.imageUri)
        assertTrue(entry.createdAt > 0)
        assertTrue(entry.updatedAt > 0)
        assertEquals(entry.createdAt, entry.updatedAt) // Should be same initially
    }

    @Test
    fun `test JournalEntry equality`() {
        val entry1 = JournalEntry(
            id = 1L,
            journalId = 1L,
            title = "Entry",
            content = "Content",
            date = 1000L
        )
        val entry2 = JournalEntry(
            id = 1L,
            journalId = 1L,
            title = "Entry",
            content = "Content",
            date = 1000L
        )
        val entry3 = JournalEntry(
            id = 2L,
            journalId = 1L,
            title = "Entry",
            content = "Content",
            date = 1000L
        )

        assertEquals(entry1, entry2)
        assertNotEquals(entry1, entry3)
    }

    @Test
    fun `test JournalEntry data class copy`() {
        val originalEntry = JournalEntry(
            id = 1L,
            journalId = 1L,
            title = "Original",
            content = "Original content",
            date = 1000L,
            mood = 50
        )
        val copiedEntry = originalEntry.copy(
            title = "Updated",
            mood = 75,
            updatedAt = 2000L
        )

        assertEquals(1L, copiedEntry.id)
        assertEquals(1L, copiedEntry.journalId)
        assertEquals("Updated", copiedEntry.title)
        assertEquals("Original content", copiedEntry.content)
        assertEquals(75, copiedEntry.mood)
        assertEquals(2000L, copiedEntry.updatedAt)
    }

    @Test
    fun `test JournalEntry with mood value`() {
        val entry = JournalEntry(
            journalId = 1L,
            title = "Entry",
            content = "Content",
            date = 1000L,
            mood = 85
        )

        assertEquals(85, entry.mood)
    }

    @Test
    fun `test JournalEntry with image URI`() {
        val entry = JournalEntry(
            journalId = 1L,
            title = "Entry",
            content = "Content",
            date = 1000L,
            imageUri = "content://photo.jpg"
        )

        assertEquals("content://photo.jpg", entry.imageUri)
    }

    @Test
    fun `test JournalEntry without mood and image`() {
        val entry = JournalEntry(
            journalId = 1L,
            title = "Entry",
            content = "Content",
            date = 1000L
        )

        assertNull(entry.mood)
        assertNull(entry.imageUri)
    }

    @Test
    fun `test JournalEntry mood boundary values`() {
        val entryMin = JournalEntry(
            journalId = 1L,
            title = "Entry",
            content = "Content",
            date = 1000L,
            mood = 0
        )
        val entryMax = JournalEntry(
            journalId = 1L,
            title = "Entry",
            content = "Content",
            date = 1000L,
            mood = 100
        )

        assertEquals(0, entryMin.mood)
        assertEquals(100, entryMax.mood)
    }
}

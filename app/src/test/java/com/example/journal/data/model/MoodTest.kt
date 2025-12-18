package com.example.journal.data.model

import org.junit.Assert.*
import org.junit.Test

class MoodTest {

    @Test
    fun `test Mood creation with all properties`() {
        val mood = Mood(
            value = 75,
            label = "Happy",
            emoji = "😊"
        )

        assertEquals(75, mood.value)
        assertEquals("Happy", mood.label)
        assertEquals("😊", mood.emoji)
    }

    @Test
    fun `test Mood creation without emoji`() {
        val mood = Mood(
            value = 50,
            label = "Neutral"
        )

        assertEquals(50, mood.value)
        assertEquals("Neutral", mood.label)
        assertNull(mood.emoji)
    }

    @Test
    fun `test Mood with minimum value`() {
        val mood = Mood(value = 0, label = "Very Unsatisfied")
        assertEquals(0, mood.value)
    }

    @Test
    fun `test Mood with maximum value`() {
        val mood = Mood(value = 100, label = "Very Satisfied")
        assertEquals(100, mood.value)
    }

    @Test
    fun `test Mood equality`() {
        val mood1 = Mood(value = 75, label = "Happy", emoji = "😊")
        val mood2 = Mood(value = 75, label = "Happy", emoji = "😊")
        val mood3 = Mood(value = 75, label = "Happy")

        assertEquals(mood1, mood2)
        assertNotEquals(mood1, mood3)
    }

    @Test
    fun `test Mood data class copy`() {
        val originalMood = Mood(value = 50, label = "Neutral", emoji = "😐")
        val copiedMood = originalMood.copy(value = 75, label = "Happy")

        assertEquals(75, copiedMood.value)
        assertEquals("Happy", copiedMood.label)
        assertEquals("😐", copiedMood.emoji) // Emoji should remain unchanged
    }
}

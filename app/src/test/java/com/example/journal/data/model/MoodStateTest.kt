package com.example.journal.data.model

import org.junit.Assert.*
import org.junit.Test

class MoodStateTest {


    @Test
    fun `test fromMoodValue returns VeryUnsatisfied for values less than or equal to 20`() {
        assertEquals(MoodState.VeryUnsatisfied, MoodState.fromMoodValue(0f))
        assertEquals(MoodState.VeryUnsatisfied, MoodState.fromMoodValue(10f))
        assertEquals(MoodState.VeryUnsatisfied, MoodState.fromMoodValue(20f))
        assertEquals(MoodState.VeryUnsatisfied, MoodState.fromMoodValue(-5f))
    }

    @Test
    fun `test fromMoodValue returns Unsatisfied for values between 20 and 40`() {
        assertEquals(MoodState.Unsatisfied, MoodState.fromMoodValue(21f))
        assertEquals(MoodState.Unsatisfied, MoodState.fromMoodValue(30f))
        assertEquals(MoodState.Unsatisfied, MoodState.fromMoodValue(40f))
    }

    @Test
    fun `test fromMoodValue returns Neutral for values between 40 and 60`() {
        assertEquals(MoodState.Neutral, MoodState.fromMoodValue(41f))
        assertEquals(MoodState.Neutral, MoodState.fromMoodValue(50f))
        assertEquals(MoodState.Neutral, MoodState.fromMoodValue(60f))
    }

    @Test
    fun `test fromMoodValue returns Satisfied for values between 60 and 80`() {
        assertEquals(MoodState.Satisfied, MoodState.fromMoodValue(61f))
        assertEquals(MoodState.Satisfied, MoodState.fromMoodValue(70f))
        assertEquals(MoodState.Satisfied, MoodState.fromMoodValue(80f))
    }

    @Test
    fun `test fromMoodValue returns VerySatisfied for values greater than 80`() {
        assertEquals(MoodState.VerySatisfied, MoodState.fromMoodValue(81f))
        assertEquals(MoodState.VerySatisfied, MoodState.fromMoodValue(90f))
        assertEquals(MoodState.VerySatisfied, MoodState.fromMoodValue(100f))
        assertEquals(MoodState.VerySatisfied, MoodState.fromMoodValue(150f))
    }

    @Test
    fun `test fromMoodValue boundary conditions`() {
        // Test exact boundary values
        assertEquals(MoodState.VeryUnsatisfied, MoodState.fromMoodValue(20f))
        assertEquals(MoodState.Unsatisfied, MoodState.fromMoodValue(20.1f))
        assertEquals(MoodState.Unsatisfied, MoodState.fromMoodValue(40f))
        assertEquals(MoodState.Neutral, MoodState.fromMoodValue(40.1f))
        assertEquals(MoodState.Neutral, MoodState.fromMoodValue(60f))
        assertEquals(MoodState.Satisfied, MoodState.fromMoodValue(60.1f))
        assertEquals(MoodState.Satisfied, MoodState.fromMoodValue(80f))
        assertEquals(MoodState.VerySatisfied, MoodState.fromMoodValue(80.1f))
    }

    @Test
    fun `test all descriptors are unique within each mood state`() {
        val allMoodStates = listOf(
            MoodState.VeryUnsatisfied,
            MoodState.Unsatisfied,
            MoodState.Neutral,
            MoodState.Satisfied,
            MoodState.VerySatisfied
        )

        allMoodStates.forEach { moodState ->
            val descriptors = moodState.descriptors
            val uniqueDescriptors = descriptors.toSet()
            assertEquals("Each mood state should have unique descriptors", descriptors.size, uniqueDescriptors.size)
        }
    }

    @Test
    fun `test all mood states have non-empty labels`() {
        val allMoodStates = listOf(
            MoodState.VeryUnsatisfied,
            MoodState.Unsatisfied,
            MoodState.Neutral,
            MoodState.Satisfied,
            MoodState.VerySatisfied
        )

        allMoodStates.forEach { moodState ->
            assertTrue("Label should not be empty", moodState.label.isNotEmpty())
            assertTrue("Asset path should not be empty", moodState.assetPath.isNotEmpty())
        }
    }

    @Test
    fun `test asset paths have correct format`() {
        val allMoodStates = listOf(
            MoodState.VeryUnsatisfied,
            MoodState.Unsatisfied,
            MoodState.Neutral,
            MoodState.Satisfied,
            MoodState.VerySatisfied
        )

        allMoodStates.forEach { moodState ->
            assertTrue(
                "Asset path should end with .lottie",
                moodState.assetPath.endsWith(".lottie")
            )
        }
    }
}

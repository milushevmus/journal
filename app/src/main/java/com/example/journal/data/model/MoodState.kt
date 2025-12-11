package com.example.journal.data.model

enum class MoodState(
    val label: String,
    val assetPath: String,
    val descriptors: List<String>
) {
    VeryUnsatisfied(
        "Very Unsatisfied",
        "veryUnsatisfied.lottie",
        listOf("Angry", "Frustrated", "Overwhelmed", "Stressed", "Anxious", "Sad", "Depressed", "Hopeless", "Exhausted", "Disappointed")
    ),
    Unsatisfied(
        "Unsatisfied",
        "unsatisfied.lottie",
        listOf("Tired", "Worried", "Uncomfortable", "Restless", "Irritated", "Lonely", "Confused", "Uncertain", "Bored", "Disconnected")
    ),
    Neutral(
        "Neutral",
        "neutral.lottie",
        listOf("Content", "Calm", "Peaceful", "Indifferent", "Drained", "Balanced", "Stable", "Quiet", "Reserved", "Thoughtful")
    ),
    Satisfied(
        "Satisfied",
        "satisfied.lottie",
        listOf("Happy", "Grateful", "Proud", "Confident", "Excited", "Energetic", "Motivated", "Optimistic", "Relaxed", "Comfortable")
    ),
    VerySatisfied(
        "Very Satisfied",
        "verySatisfied.lottie",
        listOf("Joyful", "Elated", "Ecstatic", "Blissful", "Triumphant", "Inspired", "Fulfilled", "Loved", "Grateful", "Euphoric")
    );
    
    companion object {
        fun fromMoodValue(moodValue: Float): MoodState {
            return when {
                moodValue <= 20f -> VeryUnsatisfied
                moodValue <= 40f -> Unsatisfied
                moodValue <= 60f -> Neutral
                moodValue <= 80f -> Satisfied
                else -> VerySatisfied
            }
        }
    }
}

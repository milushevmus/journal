package com.example.journal.data.model

data class Mood(
    val value: Int, // 0-100
    val label: String, // Specific mood
    val emoji: String? = null // Optional emoji representation
)

package com.example.journal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journals")
data class Journal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: String = "#6650a4", // Default purple color
    val icon: String = "description", // Icon identifier
    val createdAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false // For "Recently Deleted" special journal
)


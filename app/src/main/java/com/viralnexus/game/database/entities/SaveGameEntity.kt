package com.viralnexus.game.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity for saved games
 * Stores complete game state as JSON for persistence
 */
@Entity(tableName = "saved_games")
data class SaveGameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Save metadata
    val saveName: String,
    val isAutoSave: Boolean = false,
    val savedAt: Long = System.currentTimeMillis(),

    // Pathogen info
    val pathogenType: String,
    val pathogenName: String,

    // Game state (serialized as JSON)
    val gameStateJson: String,

    // Quick stats for display (without deserializing)
    val totalInfected: Long,
    val totalDead: Long,
    val dnaPoints: Int,
    val elapsedDays: Int,
    val cureProgress: Float,
    val infectedCountries: Int,

    // Game difficulty
    val difficulty: String,

    // Game status
    val gameStatus: String, // IN_PROGRESS, VICTORY, DEFEAT

    // Play time in seconds
    val playTimeSeconds: Long = 0
)

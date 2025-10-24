package com.viralnexus.game.repository

import com.google.gson.Gson
import com.viralnexus.game.database.dao.SaveGameDao
import com.viralnexus.game.database.entities.SaveGameEntity
import com.viralnexus.game.models.GameState
import com.viralnexus.game.models.GameStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing saved games
 * Handles serialization/deserialization and database operations
 */
class SaveGameRepository(private val dao: SaveGameDao) {

    private val gson = Gson()

    /**
     * Get all saved games as Flow
     */
    val allSaves: Flow<List<SaveGameEntity>> = dao.getAllSaves()

    /**
     * Get manual saves only
     */
    val manualSaves: Flow<List<SaveGameEntity>> = dao.getManualSaves()

    /**
     * Get auto-saves only
     */
    val autoSaves: Flow<List<SaveGameEntity>> = dao.getAutoSaves()

    /**
     * Save a game with custom name
     * @param gameState The current game state
     * @param saveName Custom name for the save
     * @param isAutoSave Whether this is an auto-save
     * @return The ID of the newly created save
     */
    suspend fun saveGame(
        gameState: GameState,
        saveName: String = generateSaveName(gameState),
        isAutoSave: Boolean = false
    ): Long {
        val gameStateJson = gson.toJson(gameState)

        val entity = SaveGameEntity(
            saveName = saveName,
            isAutoSave = isAutoSave,
            savedAt = System.currentTimeMillis(),
            pathogenType = gameState.pathogen.type.name,
            pathogenName = gameState.pathogen.name,
            gameStateJson = gameStateJson,
            totalInfected = gameState.countries.sumOf { it.infected },
            totalDead = gameState.countries.sumOf { it.dead },
            dnaPoints = gameState.dnaPoints,
            elapsedDays = gameState.elapsedDays,
            cureProgress = gameState.cureProgress,
            infectedCountries = gameState.countries.count { it.infected > 0 },
            difficulty = gameState.difficulty.name,
            gameStatus = gameState.gameStatus.name,
            playTimeSeconds = gameState.elapsedTime / 1000
        )

        val saveId = dao.insertSave(entity)

        // If this is an auto-save, clean up old auto-saves (keep only 3)
        if (isAutoSave) {
            dao.deleteOldAutoSaves(keepCount = 3)
        }

        return saveId
    }

    /**
     * Load a game by ID
     * @param id The save game ID
     * @return The deserialized GameState, or null if not found
     */
    suspend fun loadGame(id: Long): GameState? {
        val entity = dao.getSaveById(id) ?: return null
        return try {
            gson.fromJson(entity.gameStateJson, GameState::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Load the most recent save
     */
    suspend fun loadMostRecentSave(): GameState? {
        val entity = dao.getMostRecentSave() ?: return null
        return try {
            gson.fromJson(entity.gameStateJson, GameState::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Delete a save game
     */
    suspend fun deleteSave(id: Long) {
        dao.deleteSaveById(id)
    }

    /**
     * Delete a save game entity
     */
    suspend fun deleteSave(save: SaveGameEntity) {
        dao.deleteSave(save)
    }

    /**
     * Delete all auto-saves
     */
    suspend fun deleteAllAutoSaves() {
        dao.deleteAllAutoSaves()
    }

    /**
     * Get total save count
     */
    suspend fun getSaveCount(): Int {
        return dao.getSaveCount()
    }

    /**
     * Get manual save count
     */
    suspend fun getManualSaveCount(): Int {
        return dao.getManualSaveCount()
    }

    /**
     * Check if there are any saved games
     */
    suspend fun hasSavedGames(): Boolean {
        return dao.getSaveCount() > 0
    }

    /**
     * Generate a default save name based on game state
     */
    private fun generateSaveName(gameState: GameState): String {
        val statusText = when (gameState.gameStatus) {
            GameStatus.IN_PROGRESS -> "In Progress"
            GameStatus.VICTORY -> "Victory"
            GameStatus.DEFEAT -> "Defeat"
        }

        val dateTime = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())

        return "${gameState.pathogen.name} - $statusText - $dateTime"
    }

    /**
     * Update an existing save
     */
    suspend fun updateSave(
        saveId: Long,
        gameState: GameState,
        saveName: String? = null
    ) {
        val existingSave = dao.getSaveById(saveId) ?: return

        val gameStateJson = gson.toJson(gameState)

        val updatedEntity = existingSave.copy(
            saveName = saveName ?: existingSave.saveName,
            savedAt = System.currentTimeMillis(),
            gameStateJson = gameStateJson,
            totalInfected = gameState.countries.sumOf { it.infected },
            totalDead = gameState.countries.sumOf { it.dead },
            dnaPoints = gameState.dnaPoints,
            elapsedDays = gameState.elapsedDays,
            cureProgress = gameState.cureProgress,
            infectedCountries = gameState.countries.count { it.infected > 0 },
            gameStatus = gameState.gameStatus.name,
            playTimeSeconds = gameState.elapsedTime / 1000
        )

        dao.updateSave(updatedEntity)
    }
}

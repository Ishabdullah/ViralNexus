package com.viralnexus.game.database.dao

import androidx.room.*
import com.viralnexus.game.database.entities.SaveGameEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for saved games
 * Provides database operations for game saves
 */
@Dao
interface SaveGameDao {

    /**
     * Get all saved games, sorted by most recent first
     */
    @Query("SELECT * FROM saved_games ORDER BY savedAt DESC")
    fun getAllSaves(): Flow<List<SaveGameEntity>>

    /**
     * Get all manual saves (excluding auto-saves)
     */
    @Query("SELECT * FROM saved_games WHERE isAutoSave = 0 ORDER BY savedAt DESC")
    fun getManualSaves(): Flow<List<SaveGameEntity>>

    /**
     * Get all auto-saves
     */
    @Query("SELECT * FROM saved_games WHERE isAutoSave = 1 ORDER BY savedAt DESC LIMIT 3")
    fun getAutoSaves(): Flow<List<SaveGameEntity>>

    /**
     * Get a specific save by ID
     */
    @Query("SELECT * FROM saved_games WHERE id = :id")
    suspend fun getSaveById(id: Long): SaveGameEntity?

    /**
     * Get the most recent save
     */
    @Query("SELECT * FROM saved_games ORDER BY savedAt DESC LIMIT 1")
    suspend fun getMostRecentSave(): SaveGameEntity?

    /**
     * Insert a new save game
     * @return the ID of the newly inserted save
     */
    @Insert
    suspend fun insertSave(save: SaveGameEntity): Long

    /**
     * Update an existing save game
     */
    @Update
    suspend fun updateSave(save: SaveGameEntity)

    /**
     * Delete a save game
     */
    @Delete
    suspend fun deleteSave(save: SaveGameEntity)

    /**
     * Delete a save by ID
     */
    @Query("DELETE FROM saved_games WHERE id = :id")
    suspend fun deleteSaveById(id: Long)

    /**
     * Delete all auto-saves
     */
    @Query("DELETE FROM saved_games WHERE isAutoSave = 1")
    suspend fun deleteAllAutoSaves()

    /**
     * Delete old auto-saves, keeping only the most recent N
     */
    @Query("""
        DELETE FROM saved_games
        WHERE isAutoSave = 1
        AND id NOT IN (
            SELECT id FROM saved_games
            WHERE isAutoSave = 1
            ORDER BY savedAt DESC
            LIMIT :keepCount
        )
    """)
    suspend fun deleteOldAutoSaves(keepCount: Int = 3)

    /**
     * Get total count of saved games
     */
    @Query("SELECT COUNT(*) FROM saved_games")
    suspend fun getSaveCount(): Int

    /**
     * Get count of manual saves
     */
    @Query("SELECT COUNT(*) FROM saved_games WHERE isAutoSave = 0")
    suspend fun getManualSaveCount(): Int
}

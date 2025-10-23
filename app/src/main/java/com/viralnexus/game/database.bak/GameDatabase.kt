package com.viralnexus.game.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.viralnexus.game.database.dao.SaveGameDao
import com.viralnexus.game.database.entities.SaveGameEntity

/**
 * Room database for ViralNexus
 * Handles persistence of saved games
 */
@Database(
    entities = [SaveGameEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {

    /**
     * Get the DAO for saved games
     */
    abstract fun saveGameDao(): SaveGameDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        /**
         * Get the singleton database instance
         * Uses double-check locking pattern for thread safety
         */
        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "viralnexus_database"
                )
                    .fallbackToDestructiveMigration() // For development - removes this in production
                    .build()

                INSTANCE = instance
                instance
            }
        }

        /**
         * Clear the database instance (for testing)
         */
        fun clearInstance() {
            INSTANCE = null
        }
    }
}

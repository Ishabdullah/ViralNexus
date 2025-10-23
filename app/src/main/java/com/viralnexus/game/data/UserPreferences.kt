package com.viralnexus.game.data

import android.content.Context
import android.content.SharedPreferences
import com.viralnexus.game.models.Difficulty

/**
 * User preferences manager using SharedPreferences
 * Persists user settings across app sessions
 */
class UserPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "viralnexus_preferences",
        Context.MODE_PRIVATE
    )

    // Audio Settings
    var masterVolume: Float
        get() = prefs.getFloat(KEY_MASTER_VOLUME, 0.7f)
        set(value) = prefs.edit().putFloat(KEY_MASTER_VOLUME, value.coerceIn(0f, 1f)).apply()

    var musicVolume: Float
        get() = prefs.getFloat(KEY_MUSIC_VOLUME, 0.5f)
        set(value) = prefs.edit().putFloat(KEY_MUSIC_VOLUME, value.coerceIn(0f, 1f)).apply()

    var sfxVolume: Float
        get() = prefs.getFloat(KEY_SFX_VOLUME, 0.7f)
        set(value) = prefs.edit().putFloat(KEY_SFX_VOLUME, value.coerceIn(0f, 1f)).apply()

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION, value).apply()

    // Graphics Settings
    var graphicsQuality: GraphicsQuality
        get() {
            val value = prefs.getString(KEY_GRAPHICS_QUALITY, GraphicsQuality.MEDIUM.name)
            return try {
                GraphicsQuality.valueOf(value ?: GraphicsQuality.MEDIUM.name)
            } catch (e: IllegalArgumentException) {
                GraphicsQuality.MEDIUM
            }
        }
        set(value) = prefs.edit().putString(KEY_GRAPHICS_QUALITY, value.name).apply()

    var particleEffects: Boolean
        get() = prefs.getBoolean(KEY_PARTICLE_EFFECTS, true)
        set(value) = prefs.edit().putBoolean(KEY_PARTICLE_EFFECTS, value).apply()

    var screenShake: Boolean
        get() = prefs.getBoolean(KEY_SCREEN_SHAKE, true)
        set(value) = prefs.edit().putBoolean(KEY_SCREEN_SHAKE, value).apply()

    // Gameplay Settings
    var defaultDifficulty: Difficulty
        get() {
            val value = prefs.getString(KEY_DEFAULT_DIFFICULTY, Difficulty.NORMAL.name)
            return try {
                Difficulty.valueOf(value ?: Difficulty.NORMAL.name)
            } catch (e: IllegalArgumentException) {
                Difficulty.NORMAL
            }
        }
        set(value) = prefs.edit().putString(KEY_DEFAULT_DIFFICULTY, value.name).apply()

    var autoSaveEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SAVE, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_SAVE, value).apply()

    var showTutorial: Boolean
        get() = prefs.getBoolean(KEY_SHOW_TUTORIAL, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_TUTORIAL, value).apply()

    var newsTicker: Boolean
        get() = prefs.getBoolean(KEY_NEWS_TICKER, true)
        set(value) = prefs.edit().putBoolean(KEY_NEWS_TICKER, value).apply()

    var tutorialCompleted: Boolean
        get() = prefs.getBoolean(KEY_TUTORIAL_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_TUTORIAL_COMPLETED, value).apply()

    // Statistics
    var gamesPlayed: Int
        get() = prefs.getInt(KEY_GAMES_PLAYED, 0)
        set(value) = prefs.edit().putInt(KEY_GAMES_PLAYED, value).apply()

    var gamesWon: Int
        get() = prefs.getInt(KEY_GAMES_WON, 0)
        set(value) = prefs.edit().putInt(KEY_GAMES_WON, value).apply()

    /**
     * Reset tutorial to show again
     */
    fun resetTutorial() {
        tutorialCompleted = false
        showTutorial = true
    }

    /**
     * Reset all settings to defaults
     */
    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }

    /**
     * Increment games played counter
     */
    fun incrementGamesPlayed() {
        gamesPlayed += 1
    }

    /**
     * Increment games won counter
     */
    fun incrementGamesWon() {
        gamesWon += 1
    }

    companion object {
        // Audio Keys
        private const val KEY_MASTER_VOLUME = "master_volume"
        private const val KEY_MUSIC_VOLUME = "music_volume"
        private const val KEY_SFX_VOLUME = "sfx_volume"
        private const val KEY_VIBRATION = "vibration"

        // Graphics Keys
        private const val KEY_GRAPHICS_QUALITY = "graphics_quality"
        private const val KEY_PARTICLE_EFFECTS = "particle_effects"
        private const val KEY_SCREEN_SHAKE = "screen_shake"

        // Gameplay Keys
        private const val KEY_DEFAULT_DIFFICULTY = "default_difficulty"
        private const val KEY_AUTO_SAVE = "auto_save"
        private const val KEY_SHOW_TUTORIAL = "show_tutorial"
        private const val KEY_NEWS_TICKER = "news_ticker"
        private const val KEY_TUTORIAL_COMPLETED = "tutorial_completed"

        // Statistics Keys
        private const val KEY_GAMES_PLAYED = "games_played"
        private const val KEY_GAMES_WON = "games_won"
    }
}

enum class GraphicsQuality {
    LOW,
    MEDIUM,
    HIGH;

    val displayName: String
        get() = when (this) {
            LOW -> "Low"
            MEDIUM -> "Medium"
            HIGH -> "High"
        }
}

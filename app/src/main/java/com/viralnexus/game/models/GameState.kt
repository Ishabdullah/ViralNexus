package com.viralnexus.game.models

import java.util.concurrent.TimeUnit

/**
 * Represents the overall state of a game session
 *
 * @property gameId Unique identifier for this game session
 * @property pathogen The pathogen being evolved
 * @property countries List of all countries in the world
 * @property difficulty Difficulty level
 * @property startTime Game start timestamp (milliseconds)
 * @property currentTime Current game time (milliseconds)
 * @property isPaused Whether the game is paused
 * @property gameSpeed Current game speed multiplier
 * @property cureProgress Cure research progress (0.0 to 1.0)
 * @property cureStarted Whether cure research has begun
 * @property infectedNoticed Whether infection has been noticed globally
 * @property newsEvents List of news events that have occurred
 * @property totalInfected Total people infected across all countries
 * @property totalDead Total deaths across all countries
 * @property totalCured Total people cured across all countries
 */
data class GameState(
    val gameId: String,
    val pathogen: Pathogen,
    val countries: List<Country>,
    val difficulty: Difficulty,
    val startTime: Long = System.currentTimeMillis(),
    var currentTime: Long = startTime,
    var isPaused: Boolean = false,
    var gameSpeed: GameSpeed = GameSpeed.NORMAL,
    var cureProgress: Float = 0f,
    var cureStarted: Boolean = false,
    var infectedNoticed: Boolean = false,
    val newsEvents: MutableList<NewsEvent> = mutableListOf()
) {
    /**
     * Calculate total infected across all countries
     */
    val totalInfected: Long
        get() = countries.sumOf { it.infected }

    /**
     * Calculate total deaths across all countries
     */
    val totalDead: Long
        get() = countries.sumOf { it.dead }

    /**
     * Calculate total cured across all countries
     */
    val totalCured: Long
        get() = countries.sumOf { it.cured }

    /**
     * Calculate total healthy people across all countries
     */
    val totalHealthy: Long
        get() = countries.sumOf { it.healthy }

    /**
     * Get world population
     */
    val worldPopulation: Long
        get() = countries.sumOf { it.population }

    /**
     * Calculate global infection rate (0.0 to 1.0)
     */
    val globalInfectionRate: Float
        get() = if (worldPopulation > 0) totalInfected.toFloat() / worldPopulation.toFloat() else 0f

    /**
     * Calculate global death rate (0.0 to 1.0)
     */
    val globalDeathRate: Float
        get() = if (worldPopulation > 0) totalDead.toFloat() / worldPopulation.toFloat() else 0f

    /**
     * Get number of infected countries
     */
    val infectedCountries: Int
        get() = countries.count { it.infected > 0 }

    /**
     * Get number of fully infected countries
     */
    val fullyInfectedCountries: Int
        get() = countries.count { it.isFullyInfected }

    /**
     * Check if the game is won (everyone infected/dead, cure not complete)
     */
    fun checkVictory(): Boolean {
        // Must have infected or killed everyone
        val allAffected = totalHealthy == 0L

        // Cure must not be complete
        val cureNotComplete = cureProgress < 1.0f

        return allAffected && cureNotComplete
    }

    /**
     * Check if the game is lost (cure completed before infecting everyone)
     */
    fun checkDefeat(): Boolean {
        return cureProgress >= 1.0f && totalHealthy > 0
    }

    /**
     * Get game status
     */
    fun getGameStatus(): GameStatus {
        return when {
            checkVictory() -> GameStatus.VICTORY
            checkDefeat() -> GameStatus.DEFEAT
            else -> GameStatus.IN_PROGRESS
        }
    }

    /**
     * Get elapsed game time in days
     */
    fun getElapsedDays(): Int {
        val elapsedMillis = currentTime - startTime
        return TimeUnit.MILLISECONDS.toDays(elapsedMillis).toInt()
    }

    /**
     * Get a country by ID
     */
    fun getCountry(countryId: String): Country? {
        return countries.find { it.id == countryId }
    }

    /**
     * Add a news event
     */
    fun addNewsEvent(event: NewsEvent) {
        newsEvents.add(event)

        // Keep only last 50 events
        if (newsEvents.size > 50) {
            newsEvents.removeAt(0)
        }
    }

    /**
     * Update cure research progress
     */
    fun updateCureProgress(delta: Float) {
        if (cureStarted) {
            cureProgress = (cureProgress + delta).coerceIn(0f, 1f)
        }
    }

    /**
     * Start cure research if conditions are met
     */
    fun checkCureStart() {
        if (!cureStarted) {
            // Cure starts when infection is noticed globally
            // This happens when multiple countries report significant infections
            val noticedCountries = countries.count { it.isNoticed }

            if (noticedCountries >= 3) {
                cureStarted = true
                infectedNoticed = true
                addNewsEvent(
                    NewsEvent(
                        timestamp = currentTime,
                        title = "Global Pandemic Declared",
                        description = "The WHO has declared a global pandemic. Cure research has begun.",
                        severity = NewsSeverity.CRITICAL
                    )
                )
            }
        }
    }

    /**
     * Calculate total cure research rate per tick
     */
    fun calculateCureResearchRate(): Float {
        if (!cureStarted) return 0f

        var totalRate = 0f

        // Sum up contributions from all countries
        countries.forEach { country ->
            totalRate += country.calculateResearchContribution()
        }

        // Apply difficulty modifier
        totalRate *= difficulty.cureSpeedMultiplier

        // Drug resistance slows cure
        val drugResistanceFactor = 1.0f - (pathogen.drugResistance * 0.15f)
        totalRate *= drugResistanceFactor.coerceAtLeast(0.3f)

        return totalRate
    }
}

/**
 * Game difficulty levels
 */
enum class Difficulty(
    val displayName: String,
    val description: String,
    val cureSpeedMultiplier: Float,
    val startingDNA: Int
) {
    EASY(
        displayName = "Easy",
        description = "Slower cure research, more starting DNA",
        cureSpeedMultiplier = 0.7f,
        startingDNA = 25
    ),
    NORMAL(
        displayName = "Normal",
        description = "Balanced gameplay",
        cureSpeedMultiplier = 1.0f,
        startingDNA = 20
    ),
    HARD(
        displayName = "Hard",
        description = "Faster cure research, less starting DNA",
        cureSpeedMultiplier = 1.5f,
        startingDNA = 15
    ),
    BRUTAL(
        displayName = "Brutal",
        description = "Very fast cure research, minimal starting DNA",
        cureSpeedMultiplier = 2.0f,
        startingDNA = 10
    )
}

/**
 * Game speed settings
 */
enum class GameSpeed(
    val displayName: String,
    val multiplier: Float
) {
    PAUSED("Paused", 0f),
    SLOW("Slow", 0.5f),
    NORMAL("Normal", 1.0f),
    FAST("Fast", 2.0f),
    ULTRA("Ultra", 4.0f)
}

/**
 * Game status
 */
enum class GameStatus {
    IN_PROGRESS,
    VICTORY,
    DEFEAT
}

/**
 * News event in the game
 */
data class NewsEvent(
    val timestamp: Long,
    val title: String,
    val description: String,
    val severity: NewsSeverity
)

/**
 * News event severity levels
 */
enum class NewsSeverity {
    INFO,       // General information
    WARNING,    // Something concerning
    CRITICAL    // Major event
}

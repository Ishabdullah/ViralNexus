package com.viralnexus.game.engine

import com.viralnexus.game.models.*
import java.util.UUID

/**
 * Main game manager that coordinates all game systems
 * This is the primary interface for game control
 */
class GameManager {

    private var currentGame: GameState? = null
    private val infectionEngine = InfectionEngine()
    private var lastUpdateTime = System.currentTimeMillis()

    // Game callbacks
    var onGameOver: ((GameStatus) -> Unit)? = null
    var onNewsEvent: ((NewsEvent) -> Unit)? = null
    var onCountryInfected: ((Country) -> Unit)? = null
    var onDNAAwarded: ((Int) -> Unit)? = null

    /**
     * Start a new game
     */
    fun startNewGame(
        pathogenType: PathogenType,
        pathogenName: String,
        difficulty: Difficulty,
        startingCountryId: String
    ): GameState {
        // Create pathogen
        val pathogen = Pathogen(
            type = pathogenType,
            name = pathogenName,
            dnaPoints = pathogenType.startingDNA + difficulty.startingDNA
        )

        // Initialize countries
        val countries = CountryFactory.createAllCountries()

        // Create game state
        val gameState = GameState(
            gameId = UUID.randomUUID().toString(),
            pathogen = pathogen,
            countries = countries,
            difficulty = difficulty
        )

        // Infect starting country
        infectionEngine.infectCountry(gameState, startingCountryId, initialInfected = 1L)

        currentGame = gameState
        lastUpdateTime = System.currentTimeMillis()

        return gameState
    }

    /**
     * Update the game simulation
     * Call this from your game loop
     */
    fun update() {
        val game = currentGame ?: return

        // Skip if paused or game over
        if (game.isPaused || game.getGameStatus() != GameStatus.IN_PROGRESS) {
            return
        }

        // Calculate delta time
        val currentTime = System.currentTimeMillis()
        val deltaTimeMs = currentTime - lastUpdateTime
        val deltaTime = deltaTimeMs / 1000f
        lastUpdateTime = currentTime

        // Track DNA before update
        val dnaBefore = game.pathogen.dnaPoints

        // Run simulation tick
        infectionEngine.simulateTick(game, deltaTime)

        // Track DNA after update
        val dnaAfter = game.pathogen.dnaPoints
        if (dnaAfter > dnaBefore) {
            onDNAAwarded?.invoke(dnaAfter - dnaBefore)
        }

        // Check for new news events
        if (game.newsEvents.isNotEmpty()) {
            val latestEvent = game.newsEvents.last()
            onNewsEvent?.invoke(latestEvent)
        }

        // Check game over conditions
        val status = game.getGameStatus()
        if (status != GameStatus.IN_PROGRESS) {
            onGameOver?.invoke(status)
        }
    }

    /**
     * Purchase an upgrade for the pathogen
     * @return true if purchase successful
     */
    fun purchaseUpgrade(upgradeId: String): Boolean {
        val game = currentGame ?: return false
        val upgrade = UpgradeFactory.getUpgradeById(upgradeId) ?: return false

        val success = game.pathogen.purchaseUpgrade(upgrade)

        if (success) {
            // Add news event for significant upgrades
            if (upgrade.tier >= 2) {
                game.addNewsEvent(
                    NewsEvent(
                        timestamp = game.currentTime,
                        title = "Pathogen Evolution Detected",
                        description = "${game.pathogen.name} has evolved: ${upgrade.name}",
                        severity = if (upgrade.tier >= 3) NewsSeverity.CRITICAL else NewsSeverity.WARNING
                    )
                )
            }
        }

        return success
    }

    /**
     * Get list of available upgrades that can be purchased
     */
    fun getAvailableUpgrades(): List<Upgrade> {
        val game = currentGame ?: return emptyList()
        return UpgradeFactory.getAllUpgrades().filter { upgrade ->
            upgrade.isAvailable(game.pathogen) && upgrade.canAfford(game.pathogen)
        }
    }

    /**
     * Get list of all upgrades filtered by category
     */
    fun getUpgradesByCategory(category: UpgradeCategory): List<Upgrade> {
        return UpgradeFactory.getUpgradesByCategory(category)
    }

    /**
     * Check if an upgrade is available to purchase
     */
    fun isUpgradeAvailable(upgradeId: String): Boolean {
        val game = currentGame ?: return false
        val upgrade = UpgradeFactory.getUpgradeById(upgradeId) ?: return false
        return upgrade.isAvailable(game.pathogen)
    }

    /**
     * Check if player can afford an upgrade
     */
    fun canAffordUpgrade(upgradeId: String): Boolean {
        val game = currentGame ?: return false
        val upgrade = UpgradeFactory.getUpgradeById(upgradeId) ?: return false
        return upgrade.canAfford(game.pathogen)
    }

    /**
     * Toggle pause state
     */
    fun togglePause() {
        currentGame?.isPaused = !(currentGame?.isPaused ?: false)
    }

    /**
     * Set game speed
     */
    fun setGameSpeed(speed: GameSpeed) {
        currentGame?.gameSpeed = speed
    }

    /**
     * Get current game state
     */
    fun getCurrentGame(): GameState? {
        return currentGame
    }

    /**
     * Get game statistics for display
     */
    fun getGameStatistics(): GameStatistics? {
        val game = currentGame ?: return null

        return GameStatistics(
            totalInfected = game.totalInfected,
            totalDead = game.totalDead,
            totalCured = game.totalCured,
            totalHealthy = game.totalHealthy,
            worldPopulation = game.worldPopulation,
            infectedCountries = game.infectedCountries,
            fullyInfectedCountries = game.fullyInfectedCountries,
            globalInfectionRate = game.globalInfectionRate,
            globalDeathRate = game.globalDeathRate,
            cureProgress = game.cureProgress,
            dnaPoints = game.pathogen.dnaPoints,
            elapsedDays = game.getElapsedDays(),
            gameStatus = game.getGameStatus()
        )
    }

    /**
     * Reset the game manager
     */
    fun reset() {
        currentGame = null
        lastUpdateTime = System.currentTimeMillis()
    }
}

/**
 * Game statistics for UI display
 */
data class GameStatistics(
    val totalInfected: Long,
    val totalDead: Long,
    val totalCured: Long,
    val totalHealthy: Long,
    val worldPopulation: Long,
    val infectedCountries: Int,
    val fullyInfectedCountries: Int,
    val globalInfectionRate: Float,
    val globalDeathRate: Float,
    val cureProgress: Float,
    val dnaPoints: Int,
    val elapsedDays: Int,
    val gameStatus: GameStatus
)

/**
 * Factory for creating country data using complete world dataset
 */
object CountryFactory {
    fun createAllCountries(): List<Country> {
        return com.viralnexus.game.data.WorldData.getAllCountries()
    }
}

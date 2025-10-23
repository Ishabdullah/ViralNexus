package com.viralnexus.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viralnexus.game.engine.GameManager
import com.viralnexus.game.engine.GameStatistics
import com.viralnexus.game.models.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing game state and business logic
 * Survives configuration changes and provides clean separation of concerns
 */
class GameViewModel : ViewModel() {

    private val gameManager = GameManager()

    // UI State
    private val _gameStarted = MutableStateFlow(false)
    val gameStarted: StateFlow<Boolean> = _gameStarted.asStateFlow()

    private val _gameStatistics = MutableStateFlow<GameStatistics?>(null)
    val gameStatistics: StateFlow<GameStatistics?> = _gameStatistics.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    // Game loop job
    private var gameLoopJob: Job? = null

    /**
     * Start a new game with the given parameters
     */
    fun startNewGame(
        pathogenType: PathogenType,
        pathogenName: String,
        difficulty: Difficulty,
        startingCountryId: String
    ) {
        val newGame = gameManager.startNewGame(
            pathogenType = pathogenType,
            pathogenName = pathogenName,
            difficulty = difficulty,
            startingCountryId = startingCountryId
        )

        _gameState.value = newGame
        _gameStarted.value = true

        // Start game loop
        startGameLoop()
    }

    /**
     * Start the game update loop
     */
    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            var lastStatsUpdate = 0L

            while (_gameStarted.value) {
                val stats = gameManager.getGameStatistics()

                // Exit loop if game is over
                if (stats?.gameStatus != GameStatus.IN_PROGRESS) {
                    _gameStatistics.value = stats
                    break
                }

                // Update game simulation
                gameManager.update()

                // Update cached stats every 500ms
                val now = System.currentTimeMillis()
                if (now - lastStatsUpdate > 500) {
                    _gameStatistics.value = stats
                    lastStatsUpdate = now
                }

                delay(100) // Update 10 times per second
            }
        }
    }

    /**
     * Purchase an upgrade
     */
    fun purchaseUpgrade(upgradeId: String): Boolean {
        val success = gameManager.purchaseUpgrade(upgradeId)
        if (success) {
            // Force stats update
            _gameStatistics.value = gameManager.getGameStatistics()
        }
        return success
    }

    /**
     * Get available upgrades
     */
    fun getAvailableUpgrades(): List<Upgrade> {
        return gameManager.getAvailableUpgrades()
    }

    /**
     * Get upgrades by category
     */
    fun getUpgradesByCategory(category: UpgradeCategory): List<Upgrade> {
        return gameManager.getUpgradesByCategory(category)
    }

    /**
     * Toggle pause state
     */
    fun togglePause() {
        gameManager.togglePause()
        _gameState.value?.let { state ->
            _gameState.value = state.copy(isPaused = !state.isPaused)
        }
    }

    /**
     * Set game speed
     */
    fun setGameSpeed(speed: GameSpeed) {
        gameManager.setGameSpeed(speed)
        _gameState.value?.let { state ->
            _gameState.value = state.copy(gameSpeed = speed)
        }
    }

    /**
     * Select a tab
     */
    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    /**
     * Reset the game
     */
    fun resetGame() {
        gameLoopJob?.cancel()
        gameManager.reset()
        _gameStarted.value = false
        _gameStatistics.value = null
        _gameState.value = null
        _selectedTab.value = 0
    }

    /**
     * Get current game manager (for direct access if needed)
     */
    fun getGameManager(): GameManager = gameManager

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        gameManager.reset()
    }
}

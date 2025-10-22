package com.viralnexus.game.engine

import android.view.MotionEvent
import kotlin.math.*
import kotlin.random.Random

class GameEngine {
    var cameraRotationX = 0f
    var cameraRotationY = 0f
    var cameraZoom = 1f
    private var isPaused = false
    
    // Touch handling
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isRotating = false
    
    // Game state
    val gameState = GameState()
    private val worldMap = WorldMap()
    private var gameTime = 0L
    private val simulationSpeed = 1f
    
    data class GameState(
        var pathogenName: String = "Virus-X",
        var pathogenType: String = "Virus",
        var totalInfected: Long = 1L,
        var totalDead: Long = 0L,
        var dnaPoints: Int = 0,
        var severity: Float = 0f,
        var infectivity: Float = 1f,
        var lethality: Float = 0f,
        var isGameOver: Boolean = false,
        var gameSpeed: Float = 1f
    )
    
    fun update() {
        if (isPaused) return
        
        gameTime += (16 * simulationSpeed).toLong() // ~60fps
        
        // Update infection simulation
        updateInfectionSpread()
        updatePathogenEvolution()
        updateWorldResponse()
        
        // Check win/lose conditions
        checkGameEndConditions()
    }
    
    private fun updateInfectionSpread() {
        val deltaTime = 0.016f // 60fps
        var newInfections = 0L
        var newDeaths = 0L
        
        for (country in worldMap.countries) {
            if (country.infected > 0) {
                // Calculate infection spread rate
                val spreadRate = calculateSpreadRate(country)
                val newInfected = (country.infected * spreadRate * deltaTime).toLong()
                
                // Update country statistics
                country.infected = minOf(
                    country.infected + newInfected,
                    country.population - country.dead
                )
                
                // Calculate deaths
                if (gameState.lethality > 0) {
                    val deathRate = gameState.lethality * country.infected * deltaTime * 0.01f
                    val deaths = deathRate.toLong()
                    country.dead += deaths
                    country.infected -= deaths
                    newDeaths += deaths
                }
                
                newInfections += newInfected
                
                // Spread to neighboring countries
                spreadToNeighbors(country, deltaTime)
            }
        }
        
        gameState.totalInfected += newInfections
        gameState.totalDead += newDeaths
        
        // Award DNA points based on new infections
        if (newInfections > 0) {
            gameState.dnaPoints += (newInfections / 1000).toInt()
        }
    }
    
    private fun calculateSpreadRate(country: Country): Float {
        var rate = gameState.infectivity
        
        // Environmental factors
        rate *= country.climate.infectivityMultiplier
        rate *= country.wealth.resistanceMultiplier
        rate *= country.healthcare.effectivenessMultiplier
        
        // Population density effect
        rate *= (1f + country.population / 100000000f * 0.1f)
        
        // Severity penalty (too severe too early kills hosts)
        if (gameState.severity > 0.5f && gameState.totalInfected < 100000) {
            rate *= 0.5f
        }
        
        return maxOf(0.001f, rate)
    }
    
    private fun spreadToNeighbors(country: Country, deltaTime: Float) {
        for (neighbor in country.neighbors) {
            if (neighbor.infected == 0L && Random.nextFloat() < 0.1f * deltaTime) {
                // Calculate transmission probability
                val transmissionChance = gameState.infectivity * 
                    country.connectivityTo(neighbor) * 
                    deltaTime * 0.001f
                
                if (Random.nextFloat() < transmissionChance) {
                    neighbor.infected = 1L
                }
            }
        }
    }
    
    private fun updatePathogenEvolution() {
        // Natural evolution over time
        if (Random.nextFloat() < 0.001f) {
            gameState.dnaPoints += 1
        }
    }
    
    private fun updateWorldResponse() {
        // Countries respond to the pandemic
        for (country in worldMap.countries) {
            if (country.infected > country.population * 0.01) { // 1% infection rate
                country.responseLevel = minOf(country.responseLevel + 0.01f, 1f)
                
                // Implement countermeasures
                if (country.responseLevel > 0.3f) {
                    country.healthcare.effectiveness *= 1.01f
                    gameState.infectivity *= 0.999f // Slight reduction due to awareness
                }
            }
        }
    }
    
    private fun checkGameEndConditions() {
        val totalWorldPopulation = worldMap.countries.sumOf { it.population }
        val infectionRate = gameState.totalInfected.toFloat() / totalWorldPopulation
        
        // Win condition: Infect everyone
        if (infectionRate > 0.99f) {
            gameState.isGameOver = true
        }
        
        // Lose condition: No more infected and cure developed
        if (gameState.totalInfected == 0L && worldMap.countries.any { it.responseLevel > 0.8f }) {
            gameState.isGameOver = true
        }
    }
    
    fun handleTouch(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = x
                lastTouchY = y
                isRotating = true
                return true
            }
            
            MotionEvent.ACTION_MOVE -> {
                if (isRotating) {
                    val deltaX = x - lastTouchX
                    val deltaY = y - lastTouchY
                    
                    cameraRotationY += deltaX * 0.5f
                    cameraRotationX += deltaY * 0.5f
                    
                    // Clamp X rotation
                    cameraRotationX = cameraRotationX.coerceIn(-90f, 90f)
                    
                    lastTouchX = x
                    lastTouchY = y
                    return true
                }
            }
            
            MotionEvent.ACTION_UP -> {
                isRotating = false
                return true
            }
        }
        
        return false
    }
    
    fun togglePause() {
        isPaused = !isPaused
    }
    
    // Upgrade functions
    fun upgradeTransmission(type: TransmissionType): Boolean {
        val cost = type.cost
        if (gameState.dnaPoints >= cost) {
            gameState.dnaPoints -= cost
            gameState.infectivity += type.infectivityBoost
            return true
        }
        return false
    }
    
    fun upgradeSymptom(type: SymptomType): Boolean {
        val cost = type.cost
        if (gameState.dnaPoints >= cost) {
            gameState.dnaPoints -= cost
            gameState.severity += type.severityIncrease
            gameState.lethality += type.lethalityIncrease
            return true
        }
        return false
    }
}

// Game data classes
enum class TransmissionType(val cost: Int, val infectivityBoost: Float) {
    AIR_TRANSMISSION(8, 0.2f),
    WATER_TRANSMISSION(10, 0.15f),
    BLOOD_TRANSMISSION(12, 0.1f),
    INSECT_VECTOR(15, 0.25f),
    LIVESTOCK(18, 0.3f)
}

enum class SymptomType(val cost: Int, val severityIncrease: Float, val lethalityIncrease: Float) {
    COUGHING(5, 0.1f, 0f),
    FEVER(6, 0.15f, 0.02f),
    NAUSEA(7, 0.2f, 0.01f),
    PNEUMONIA(12, 0.3f, 0.1f),
    ORGAN_FAILURE(20, 0.5f, 0.3f),
    TOTAL_ORGAN_FAILURE(25, 0.8f, 0.6f)
}
package com.viralnexus.game.engine

import com.viralnexus.game.models.*
import kotlin.math.min
import kotlin.random.Random

/**
 * Core infection simulation engine
 * Handles the spread of disease, evolution, and cure research
 */
class InfectionEngine {

    companion object {
        // Simulation constants
        private const val TICKS_PER_SECOND = 10f
        private const val INFECTION_SPREAD_BASE = 0.001f
        private const val CROSS_BORDER_CHANCE_BASE = 0.0001f
        private const val AIR_TRAVEL_MULTIPLIER = 2.5f
        private const val SEA_TRAVEL_MULTIPLIER = 1.5f
        private const val DNA_PER_INFECTION_GROUP = 1000L // Award 1 DNA per 1000 infections
        private const val DNA_PER_COUNTRY_INFECTED = 5 // Bonus DNA for infecting a new country
    }

    /**
     * Simulate one game tick
     * @param gameState The current game state
     * @param deltaTime Time elapsed since last tick (in seconds)
     */
    fun simulateTick(gameState: GameState, deltaTime: Float) {
        if (gameState.isPaused || gameState.gameSpeed == GameSpeed.PAUSED) {
            return
        }

        val adjustedDelta = deltaTime * gameState.gameSpeed.multiplier

        // Update game time
        gameState.currentTime += (adjustedDelta * 1000).toLong()

        // Simulate infection spread
        simulateInfectionSpread(gameState, adjustedDelta)

        // Simulate deaths
        simulateDeaths(gameState, adjustedDelta)

        // Cross-border transmission
        simulateCrossBorderSpread(gameState, adjustedDelta)

        // Check for cure start
        gameState.checkCureStart()

        // Update cure research
        if (gameState.cureStarted) {
            val cureRate = gameState.calculateCureResearchRate()
            gameState.updateCureProgress(cureRate * adjustedDelta)
        }

        // Random mutations (award bonus DNA)
        simulateMutations(gameState, adjustedDelta)
    }

    /**
     * Simulate infection spreading within countries
     */
    private fun simulateInfectionSpread(gameState: GameState, deltaTime: Float) {
        val pathogen = gameState.pathogen
        var totalNewInfections = 0L

        gameState.countries.forEach { country ->
            if (country.infected > 0 && country.healthy > 0) {
                // Calculate base infection rate
                val baseRate = pathogen.calculateInfectivity() * INFECTION_SPREAD_BASE

                // Apply country modifiers
                val countryModifier = country.getTransmissionModifier(pathogen)

                // Calculate how many healthy people get infected
                val infectiousContact = country.infected * baseRate * countryModifier * deltaTime

                // Apply to healthy population
                val newInfections = min(
                    infectiousContact.toLong(),
                    country.healthy
                )

                if (newInfections > 0) {
                    country.infected += newInfections
                    totalNewInfections += newInfections
                }
            }
        }

        // Award DNA points for infections
        if (totalNewInfections > 0) {
            val dnaAwarded = (totalNewInfections / DNA_PER_INFECTION_GROUP).toInt()
            if (dnaAwarded > 0) {
                pathogen.awardDNA(dnaAwarded)
            }
        }
    }

    /**
     * Simulate deaths from the disease
     */
    private fun simulateDeaths(gameState: GameState, deltaTime: Float) {
        val pathogen = gameState.pathogen
        val lethality = pathogen.calculateLethality()

        if (lethality > 0) {
            gameState.countries.forEach { country ->
                if (country.infected > 0) {
                    // Death rate modified by healthcare
                    val deathModifier = 1.0f - (country.healthcareLevel * 0.5f)

                    // Calculate deaths
                    val deathRate = lethality * deathModifier * deltaTime * 0.01f
                    val deaths = (country.infected * deathRate).toLong().coerceAtLeast(0)

                    if (deaths > 0) {
                        val actualDeaths = min(deaths, country.infected)
                        country.infected -= actualDeaths
                        country.dead += actualDeaths
                    }
                }
            }
        }
    }

    /**
     * Simulate disease spreading between countries
     */
    private fun simulateCrossBorderSpread(gameState: GameState, deltaTime: Float) {
        val pathogen = gameState.pathogen

        gameState.countries.forEach { sourceCountry ->
            if (sourceCountry.infected > 0) {
                // Spread to bordering countries (land borders)
                sourceCountry.borders.forEach { borderCountryId ->
                    val targetCountry = gameState.getCountry(borderCountryId)
                    if (targetCountry != null && targetCountry.infected == 0L) {
                        val spreadChance = calculateBorderSpreadChance(
                            sourceCountry,
                            targetCountry,
                            pathogen,
                            deltaTime
                        )

                        if (Random.nextFloat() < spreadChance) {
                            infectNewCountry(targetCountry, gameState)
                        }
                    }
                }

                // Air travel spread
                if (sourceCountry.airports > 0 && pathogen.airTransmission > 0) {
                    spreadViaAirTravel(sourceCountry, gameState, deltaTime)
                }

                // Sea travel spread
                if (sourceCountry.seaports > 0 && pathogen.waterTransmission > 0) {
                    spreadViaSeaTravel(sourceCountry, gameState, deltaTime)
                }
            }
        }
    }

    /**
     * Calculate chance of spread across land border
     */
    private fun calculateBorderSpreadChance(
        source: Country,
        target: Country,
        pathogen: Pathogen,
        deltaTime: Float
    ): Float {
        var chance = CROSS_BORDER_CHANCE_BASE

        // Higher infection rate = more likely to spread
        chance *= (1.0f + source.infectionRate * 10.0f)

        // Pathogen infectivity
        chance *= pathogen.calculateInfectivity() * 10.0f

        // Urbanization increases spread
        chance *= (1.0f + source.urbanization)

        // Time factor
        chance *= deltaTime * TICKS_PER_SECOND

        return chance.coerceIn(0f, 1f)
    }

    /**
     * Spread disease via air travel
     */
    private fun spreadViaAirTravel(sourceCountry: Country, gameState: GameState, deltaTime: Float) {
        // Find potential air travel destinations (high wealth, high urbanization)
        val destinations = gameState.countries.filter { country ->
            country.id != sourceCountry.id &&
                    country.infected == 0L &&
                    country.airports > 0 &&
                    (country.wealthLevel == WealthLevel.WEALTHY || country.wealthLevel == WealthLevel.DEVELOPING)
        }

        if (destinations.isNotEmpty()) {
            val spreadChance = sourceCountry.airports *
                    gameState.pathogen.airTransmission *
                    AIR_TRAVEL_MULTIPLIER *
                    sourceCountry.infectionRate *
                    deltaTime *
                    0.0001f

            if (Random.nextFloat() < spreadChance) {
                // Pick a random wealthy destination
                val destination = destinations.random()
                infectNewCountry(destination, gameState)
            }
        }
    }

    /**
     * Spread disease via sea travel
     */
    private fun spreadViaSeaTravel(sourceCountry: Country, gameState: GameState, deltaTime: Float) {
        // Find potential sea travel destinations
        val destinations = gameState.countries.filter { country ->
            country.id != sourceCountry.id &&
                    country.infected == 0L &&
                    country.seaports > 0
        }

        if (destinations.isNotEmpty()) {
            val spreadChance = sourceCountry.seaports *
                    gameState.pathogen.waterTransmission *
                    SEA_TRAVEL_MULTIPLIER *
                    sourceCountry.infectionRate *
                    deltaTime *
                    0.0001f

            if (Random.nextFloat() < spreadChance) {
                val destination = destinations.random()
                infectNewCountry(destination, gameState)
            }
        }
    }

    /**
     * Infect a new country for the first time
     */
    private fun infectNewCountry(country: Country, gameState: GameState) {
        country.infected = 1L

        // Award bonus DNA
        gameState.pathogen.awardDNA(DNA_PER_COUNTRY_INFECTED)

        // Add news event
        gameState.addNewsEvent(
            NewsEvent(
                timestamp = gameState.currentTime,
                title = "New Country Infected",
                description = "${country.name} reports first cases of ${gameState.pathogen.name}",
                severity = if (gameState.infectedCountries > 10) NewsSeverity.CRITICAL else NewsSeverity.WARNING
            )
        )
    }

    /**
     * Simulate random mutations that award DNA
     */
    private fun simulateMutations(gameState: GameState, deltaTime: Float) {
        val mutationChance = gameState.pathogen.getMutationChance() * deltaTime

        if (Random.nextFloat() < mutationChance) {
            gameState.pathogen.awardDNA(1)
        }
    }

    /**
     * Manually infect a specific country (for game start)
     */
    fun infectCountry(gameState: GameState, countryId: String, initialInfected: Long = 1L): Boolean {
        val country = gameState.getCountry(countryId)
        if (country != null && country.infected == 0L) {
            country.infected = initialInfected
            return true
        }
        return false
    }
}

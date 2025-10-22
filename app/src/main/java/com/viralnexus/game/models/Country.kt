package com.viralnexus.game.models

/**
 * Represents a country in the game world
 *
 * @property id Unique identifier for the country
 * @property name Display name of the country
 * @property population Total population
 * @property healthcareLevel Healthcare quality (0.0 to 1.0)
 * @property climateType Climate classification (HOT, TEMPERATE, COLD)
 * @property wealthLevel Economic development (POOR, DEVELOPING, WEALTHY)
 * @property urbanization Percentage of urban population (0.0 to 1.0)
 * @property latitude Geographic latitude for map positioning
 * @property longitude Geographic longitude for map positioning
 * @property infected Number of infected individuals
 * @property dead Number of deaths
 * @property cured Number of cured individuals
 * @property researchContribution Contribution to cure research per tick
 * @property borders List of country IDs that share a border
 * @property airports Major airport count for air transmission
 * @property seaports Major seaport count for sea transmission
 */
data class Country(
    val id: String,
    val name: String,
    val population: Long,
    val healthcareLevel: Float,
    val climateType: ClimateType,
    val wealthLevel: WealthLevel,
    val urbanization: Float,
    val latitude: Float,
    val longitude: Float,
    var infected: Long = 0L,
    var dead: Long = 0L,
    var cured: Long = 0L,
    var researchContribution: Float = 0f,
    val borders: List<String> = emptyList(),
    val airports: Int = 0,
    val seaports: Int = 0
) {
    /**
     * Get the number of healthy individuals
     */
    val healthy: Long
        get() = (population - infected - dead - cured).coerceAtLeast(0)

    /**
     * Get infection percentage (0.0 to 1.0)
     */
    val infectionRate: Float
        get() = if (population > 0) infected.toFloat() / population.toFloat() else 0f

    /**
     * Get death percentage (0.0 to 1.0)
     */
    val deathRate: Float
        get() = if (population > 0) dead.toFloat() / population.toFloat() else 0f

    /**
     * Check if country is fully infected
     */
    val isFullyInfected: Boolean
        get() = infected >= population - dead - cured

    /**
     * Check if country has been noticed (5% infection threshold)
     */
    val isNoticed: Boolean
        get() = infectionRate >= 0.05f

    /**
     * Calculate transmission modifier based on country characteristics
     */
    fun getTransmissionModifier(pathogen: Pathogen): Float {
        var modifier = 1.0f

        // Healthcare reduces transmission
        modifier *= (1.0f - healthcareLevel * 0.3f)

        // Urbanization increases transmission
        modifier *= (1.0f + urbanization * 0.2f)

        // Climate affects certain pathogens
        when (climateType) {
            ClimateType.HOT -> {
                if (pathogen.heatResistance > 0) modifier *= 1.2f
                if (pathogen.coldResistance > 0) modifier *= 0.8f
            }
            ClimateType.COLD -> {
                if (pathogen.coldResistance > 0) modifier *= 1.2f
                if (pathogen.heatResistance > 0) modifier *= 0.8f
            }
            ClimateType.TEMPERATE -> {
                // Neutral climate, no major modifiers
            }
        }

        return modifier
    }

    /**
     * Calculate cure research contribution based on infection status
     */
    fun calculateResearchContribution(): Float {
        // More infected = more research effort
        val infectionFactor = infectionRate.coerceIn(0f, 1f)

        // Wealthy countries contribute more
        val wealthMultiplier = when (wealthLevel) {
            WealthLevel.POOR -> 0.5f
            WealthLevel.DEVELOPING -> 1.0f
            WealthLevel.WEALTHY -> 2.0f
        }

        // Healthcare level affects research capability
        val researchCapability = healthcareLevel * wealthMultiplier

        return infectionFactor * researchCapability * 0.1f
    }
}

/**
 * Climate classification for countries
 */
enum class ClimateType {
    HOT,        // Tropical, desert climates
    TEMPERATE,  // Moderate climates
    COLD        // Arctic, sub-arctic climates
}

/**
 * Economic development level
 */
enum class WealthLevel {
    POOR,       // Low GDP per capita
    DEVELOPING, // Middle income
    WEALTHY     // High GDP per capita
}

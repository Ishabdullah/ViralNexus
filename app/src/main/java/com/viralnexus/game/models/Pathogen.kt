package com.viralnexus.game.models

/**
 * Represents the pathogen being evolved by the player
 *
 * @property type Type of pathogen (virus, bacteria, fungus, parasite)
 * @property name Custom name given by player
 * @property dnaPoints Available DNA points for purchasing upgrades
 * @property infectivity Base infectiousness (0.0 to 1.0)
 * @property severity How severe/noticeable the disease is (0.0 to 1.0)
 * @property lethality How deadly the disease is (0.0 to 1.0)
 * @property heatResistance Resistance to hot climates (0 to 3)
 * @property coldResistance Resistance to cold climates (0 to 3)
 * @property drugResistance Resistance to medical treatment (0 to 3)
 * @property airTransmission Air transmission capability (0 to 3)
 * @property waterTransmission Water transmission capability (0 to 3)
 * @property bloodTransmission Blood transmission capability (0 to 3)
 * @property animalTransmission Animal vector capability (0 to 3)
 * @property upgrades List of purchased upgrade IDs
 */
data class Pathogen(
    val type: PathogenType,
    var name: String,
    var dnaPoints: Int = 0,

    // Core stats
    var infectivity: Float = 0.0f,
    var severity: Float = 0.0f,
    var lethality: Float = 0.0f,

    // Environmental resistance
    var heatResistance: Int = 0,
    var coldResistance: Int = 0,
    var drugResistance: Int = 0,

    // Transmission capabilities
    var airTransmission: Int = 0,
    var waterTransmission: Int = 0,
    var bloodTransmission: Int = 0,
    var animalTransmission: Int = 0,

    // Purchased upgrades
    val upgrades: MutableSet<String> = mutableSetOf()
) {
    /**
     * Calculate total infectivity including all modifiers
     */
    fun calculateInfectivity(): Float {
        var total = infectivity

        // Base type bonuses
        total += type.infectivityBonus

        // Transmission bonuses
        total += airTransmission * 0.15f
        total += waterTransmission * 0.10f
        total += bloodTransmission * 0.08f
        total += animalTransmission * 0.12f

        return total.coerceIn(0f, 1f)
    }

    /**
     * Calculate total severity including all modifiers
     */
    fun calculateSeverity(): Float {
        var total = severity

        // Base type characteristics
        total += type.severityBonus

        // Lethality increases severity
        total += lethality * 0.3f

        return total.coerceIn(0f, 1f)
    }

    /**
     * Calculate total lethality including all modifiers
     */
    fun calculateLethality(): Float {
        var total = lethality

        // Base type characteristics
        total += type.lethalityBonus

        return total.coerceIn(0f, 1f)
    }

    /**
     * Check if a specific upgrade has been purchased
     */
    fun hasUpgrade(upgradeId: String): Boolean {
        return upgrades.contains(upgradeId)
    }

    /**
     * Purchase an upgrade
     * @return true if purchase successful, false if insufficient DNA or already owned
     */
    fun purchaseUpgrade(upgrade: Upgrade): Boolean {
        // Check if already owned
        if (hasUpgrade(upgrade.id)) {
            return false
        }

        // Check prerequisites
        if (!upgrade.prerequisites.all { hasUpgrade(it) }) {
            return false
        }

        // Check DNA points
        if (dnaPoints < upgrade.cost) {
            return false
        }

        // Purchase successful
        dnaPoints -= upgrade.cost
        upgrades.add(upgrade.id)

        // Apply upgrade effects
        applyUpgradeEffects(upgrade)

        return true
    }

    /**
     * Apply the effects of an upgrade to this pathogen
     */
    private fun applyUpgradeEffects(upgrade: Upgrade) {
        infectivity += upgrade.infectivityBonus
        severity += upgrade.severityBonus
        lethality += upgrade.lethalityBonus

        heatResistance += upgrade.heatResistanceBonus
        coldResistance += upgrade.coldResistanceBonus
        drugResistance += upgrade.drugResistanceBonus

        airTransmission += upgrade.airTransmissionBonus
        waterTransmission += upgrade.waterTransmissionBonus
        bloodTransmission += upgrade.bloodTransmissionBonus
        animalTransmission += upgrade.animalTransmissionBonus
    }

    /**
     * Award DNA points (from new infections, country infections, etc.)
     */
    fun awardDNA(amount: Int) {
        dnaPoints += amount
    }

    /**
     * Calculate mutation chance based on pathogen type and current state
     */
    fun getMutationChance(): Float {
        return type.mutationRate * (1.0f + upgrades.size * 0.05f)
    }
}

/**
 * Types of pathogens with different characteristics
 */
enum class PathogenType(
    val displayName: String,
    val description: String,
    val infectivityBonus: Float,
    val severityBonus: Float,
    val lethalityBonus: Float,
    val mutationRate: Float,
    val startingDNA: Int
) {
    VIRUS(
        displayName = "Virus",
        description = "High mutation rate, moderate infectivity",
        infectivityBonus = 0.1f,
        severityBonus = 0.0f,
        lethalityBonus = 0.0f,
        mutationRate = 0.15f,
        startingDNA = 15
    ),

    BACTERIA(
        displayName = "Bacteria",
        description = "Balanced pathogen, good for beginners",
        infectivityBonus = 0.05f,
        severityBonus = 0.0f,
        lethalityBonus = 0.0f,
        mutationRate = 0.08f,
        startingDNA = 20
    ),

    FUNGUS(
        displayName = "Fungus",
        description = "Slow spreading, spore-based transmission",
        infectivityBonus = -0.05f,
        severityBonus = 0.05f,
        lethalityBonus = 0.0f,
        mutationRate = 0.05f,
        startingDNA = 25
    ),

    PARASITE(
        displayName = "Parasite",
        description = "Stealthy, harder to detect",
        infectivityBonus = 0.0f,
        severityBonus = -0.1f,
        lethalityBonus = 0.05f,
        mutationRate = 0.06f,
        startingDNA = 18
    )
}

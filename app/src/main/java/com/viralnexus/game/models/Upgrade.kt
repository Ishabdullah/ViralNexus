package com.viralnexus.game.models

/**
 * Represents an upgrade that can be purchased for the pathogen
 *
 * @property id Unique identifier for this upgrade
 * @property name Display name
 * @property description What this upgrade does
 * @property cost DNA points required to purchase
 * @property category Category (TRANSMISSION, SYMPTOMS, ABILITIES)
 * @property tier Upgrade tier (1 to 3, higher is more powerful)
 * @property prerequisites List of upgrade IDs that must be purchased first
 * @property infectivityBonus Bonus to infectivity stat
 * @property severityBonus Bonus to severity stat
 * @property lethalityBonus Bonus to lethality stat
 * @property heatResistanceBonus Bonus to heat resistance
 * @property coldResistanceBonus Bonus to cold resistance
 * @property drugResistanceBonus Bonus to drug resistance
 * @property airTransmissionBonus Bonus to air transmission
 * @property waterTransmissionBonus Bonus to water transmission
 * @property bloodTransmissionBonus Bonus to blood transmission
 * @property animalTransmissionBonus Bonus to animal transmission
 */
data class Upgrade(
    val id: String,
    val name: String,
    val description: String,
    val cost: Int,
    val category: UpgradeCategory,
    val tier: Int = 1,
    val prerequisites: List<String> = emptyList(),

    // Stat bonuses
    val infectivityBonus: Float = 0f,
    val severityBonus: Float = 0f,
    val lethalityBonus: Float = 0f,

    // Resistance bonuses
    val heatResistanceBonus: Int = 0,
    val coldResistanceBonus: Int = 0,
    val drugResistanceBonus: Int = 0,

    // Transmission bonuses
    val airTransmissionBonus: Int = 0,
    val waterTransmissionBonus: Int = 0,
    val bloodTransmissionBonus: Int = 0,
    val animalTransmissionBonus: Int = 0
) {
    /**
     * Check if this upgrade is available for purchase
     * (prerequisites met, not already owned)
     */
    fun isAvailable(pathogen: Pathogen): Boolean {
        // Already purchased
        if (pathogen.hasUpgrade(id)) {
            return false
        }

        // Check prerequisites
        return prerequisites.all { pathogen.hasUpgrade(it) }
    }

    /**
     * Check if player can afford this upgrade
     */
    fun canAfford(pathogen: Pathogen): Boolean {
        return pathogen.dnaPoints >= cost
    }

    /**
     * Get a formatted display of all bonuses this upgrade provides
     */
    fun getBonusSummary(): String {
        val bonuses = mutableListOf<String>()

        if (infectivityBonus != 0f) {
            bonuses.add("Infectivity: ${if (infectivityBonus > 0) "+" else ""}${(infectivityBonus * 100).toInt()}%")
        }
        if (severityBonus != 0f) {
            bonuses.add("Severity: ${if (severityBonus > 0) "+" else ""}${(severityBonus * 100).toInt()}%")
        }
        if (lethalityBonus != 0f) {
            bonuses.add("Lethality: ${if (lethalityBonus > 0) "+" else ""}${(lethalityBonus * 100).toInt()}%")
        }

        if (heatResistanceBonus > 0) bonuses.add("Heat Resistance: +$heatResistanceBonus")
        if (coldResistanceBonus > 0) bonuses.add("Cold Resistance: +$coldResistanceBonus")
        if (drugResistanceBonus > 0) bonuses.add("Drug Resistance: +$drugResistanceBonus")

        if (airTransmissionBonus > 0) bonuses.add("Air Transmission: +$airTransmissionBonus")
        if (waterTransmissionBonus > 0) bonuses.add("Water Transmission: +$waterTransmissionBonus")
        if (bloodTransmissionBonus > 0) bonuses.add("Blood Transmission: +$bloodTransmissionBonus")
        if (animalTransmissionBonus > 0) bonuses.add("Animal Transmission: +$animalTransmissionBonus")

        return bonuses.joinToString("\n")
    }
}

/**
 * Categories of upgrades
 */
enum class UpgradeCategory(val displayName: String) {
    TRANSMISSION("Transmission"),
    SYMPTOMS("Symptoms"),
    ABILITIES("Abilities")
}

/**
 * Factory object for creating all available upgrades
 */
object UpgradeFactory {
    /**
     * Get all available upgrades in the game
     */
    fun getAllUpgrades(): List<Upgrade> {
        return listOf(
            // TRANSMISSION - Air
            Upgrade(
                id = "air_1",
                name = "Coughing",
                description = "Pathogen spreads through coughing. Slightly increases infectivity.",
                cost = 5,
                category = UpgradeCategory.TRANSMISSION,
                tier = 1,
                infectivityBonus = 0.05f,
                airTransmissionBonus = 1
            ),
            Upgrade(
                id = "air_2",
                name = "Sneezing",
                description = "Pathogen spreads through sneezing. Increases infectivity.",
                cost = 8,
                category = UpgradeCategory.TRANSMISSION,
                tier = 2,
                prerequisites = listOf("air_1"),
                infectivityBonus = 0.08f,
                airTransmissionBonus = 1,
                severityBonus = 0.02f
            ),
            Upgrade(
                id = "air_3",
                name = "Aerosol Spread",
                description = "Pathogen becomes airborne. Greatly increases infectivity.",
                cost = 15,
                category = UpgradeCategory.TRANSMISSION,
                tier = 3,
                prerequisites = listOf("air_2"),
                infectivityBonus = 0.15f,
                airTransmissionBonus = 1,
                severityBonus = 0.03f
            ),

            // TRANSMISSION - Water
            Upgrade(
                id = "water_1",
                name = "Water Survival",
                description = "Pathogen can survive in water sources.",
                cost = 6,
                category = UpgradeCategory.TRANSMISSION,
                tier = 1,
                infectivityBonus = 0.04f,
                waterTransmissionBonus = 1
            ),
            Upgrade(
                id = "water_2",
                name = "Waterborne",
                description = "Pathogen spreads efficiently through contaminated water.",
                cost = 10,
                category = UpgradeCategory.TRANSMISSION,
                tier = 2,
                prerequisites = listOf("water_1"),
                infectivityBonus = 0.07f,
                waterTransmissionBonus = 1
            ),
            Upgrade(
                id = "water_3",
                name = "Extreme Bioaerosol",
                description = "Pathogen spreads through water vapor in the air.",
                cost = 18,
                category = UpgradeCategory.TRANSMISSION,
                tier = 3,
                prerequisites = listOf("water_2", "air_2"),
                infectivityBonus = 0.12f,
                waterTransmissionBonus = 1,
                airTransmissionBonus = 1
            ),

            // TRANSMISSION - Blood
            Upgrade(
                id = "blood_1",
                name = "Insect Bites",
                description = "Pathogen spreads through insect bites.",
                cost = 7,
                category = UpgradeCategory.TRANSMISSION,
                tier = 1,
                infectivityBonus = 0.06f,
                bloodTransmissionBonus = 1,
                animalTransmissionBonus = 1
            ),
            Upgrade(
                id = "blood_2",
                name = "Blood Transmission",
                description = "Pathogen spreads through blood contact.",
                cost = 12,
                category = UpgradeCategory.TRANSMISSION,
                tier = 2,
                prerequisites = listOf("blood_1"),
                infectivityBonus = 0.09f,
                bloodTransmissionBonus = 1,
                severityBonus = 0.05f
            ),

            // TRANSMISSION - Animals
            Upgrade(
                id = "animal_1",
                name = "Livestock Transmission",
                description = "Pathogen spreads through livestock.",
                cost = 8,
                category = UpgradeCategory.TRANSMISSION,
                tier = 1,
                infectivityBonus = 0.05f,
                animalTransmissionBonus = 1
            ),
            Upgrade(
                id = "animal_2",
                name = "Bird Transmission",
                description = "Pathogen spreads through birds. Enables long-distance spread.",
                cost = 14,
                category = UpgradeCategory.TRANSMISSION,
                tier = 2,
                prerequisites = listOf("animal_1"),
                infectivityBonus = 0.10f,
                animalTransmissionBonus = 1
            ),
            Upgrade(
                id = "animal_3",
                name = "Rodent Transmission",
                description = "Pathogen spreads through rodents in urban areas.",
                cost = 11,
                category = UpgradeCategory.TRANSMISSION,
                tier = 2,
                prerequisites = listOf("animal_1"),
                infectivityBonus = 0.08f,
                animalTransmissionBonus = 1
            ),

            // SYMPTOMS - Minor
            Upgrade(
                id = "symptom_1",
                name = "Fever",
                description = "Increases body temperature. Slightly increases severity.",
                cost = 4,
                category = UpgradeCategory.SYMPTOMS,
                tier = 1,
                severityBonus = 0.05f,
                infectivityBonus = 0.02f
            ),
            Upgrade(
                id = "symptom_2",
                name = "Fatigue",
                description = "Causes tiredness and weakness.",
                cost = 4,
                category = UpgradeCategory.SYMPTOMS,
                tier = 1,
                severityBonus = 0.04f
            ),
            Upgrade(
                id = "symptom_3",
                name = "Nausea",
                description = "Causes stomach discomfort and vomiting.",
                cost = 5,
                category = UpgradeCategory.SYMPTOMS,
                tier = 1,
                severityBonus = 0.06f,
                infectivityBonus = 0.01f
            ),

            // SYMPTOMS - Moderate
            Upgrade(
                id = "symptom_4",
                name = "Pneumonia",
                description = "Lung infection. Increases severity and lethality.",
                cost = 10,
                category = UpgradeCategory.SYMPTOMS,
                tier = 2,
                prerequisites = listOf("symptom_1", "air_1"),
                severityBonus = 0.10f,
                lethalityBonus = 0.08f
            ),
            Upgrade(
                id = "symptom_5",
                name = "Organ Failure",
                description = "Causes organ shutdown. Highly lethal.",
                cost = 18,
                category = UpgradeCategory.SYMPTOMS,
                tier = 3,
                prerequisites = listOf("symptom_1", "symptom_2"),
                severityBonus = 0.15f,
                lethalityBonus = 0.20f
            ),
            Upgrade(
                id = "symptom_6",
                name = "Hemorrhaging",
                description = "Internal bleeding. Very severe and lethal.",
                cost = 20,
                category = UpgradeCategory.SYMPTOMS,
                tier = 3,
                prerequisites = listOf("symptom_3"),
                severityBonus = 0.18f,
                lethalityBonus = 0.25f
            ),
            Upgrade(
                id = "symptom_7",
                name = "Necrosis",
                description = "Tissue death. Extremely lethal.",
                cost = 25,
                category = UpgradeCategory.SYMPTOMS,
                tier = 3,
                prerequisites = listOf("symptom_5"),
                severityBonus = 0.20f,
                lethalityBonus = 0.30f
            ),

            // ABILITIES - Environmental Resistance
            Upgrade(
                id = "ability_heat_1",
                name = "Heat Resistance 1",
                description = "Pathogen survives better in hot climates.",
                cost = 6,
                category = UpgradeCategory.ABILITIES,
                tier = 1,
                heatResistanceBonus = 1,
                infectivityBonus = 0.03f
            ),
            Upgrade(
                id = "ability_heat_2",
                name = "Heat Resistance 2",
                description = "Pathogen thrives in hot climates.",
                cost = 12,
                category = UpgradeCategory.ABILITIES,
                tier = 2,
                prerequisites = listOf("ability_heat_1"),
                heatResistanceBonus = 1,
                infectivityBonus = 0.05f
            ),
            Upgrade(
                id = "ability_cold_1",
                name = "Cold Resistance 1",
                description = "Pathogen survives better in cold climates.",
                cost = 6,
                category = UpgradeCategory.ABILITIES,
                tier = 1,
                coldResistanceBonus = 1,
                infectivityBonus = 0.03f
            ),
            Upgrade(
                id = "ability_cold_2",
                name = "Cold Resistance 2",
                description = "Pathogen thrives in cold climates.",
                cost = 12,
                category = UpgradeCategory.ABILITIES,
                tier = 2,
                prerequisites = listOf("ability_cold_1"),
                coldResistanceBonus = 1,
                infectivityBonus = 0.05f
            ),

            // ABILITIES - Drug Resistance
            Upgrade(
                id = "ability_drug_1",
                name = "Drug Resistance 1",
                description = "Pathogen becomes resistant to basic medication.",
                cost = 8,
                category = UpgradeCategory.ABILITIES,
                tier = 1,
                drugResistanceBonus = 1
            ),
            Upgrade(
                id = "ability_drug_2",
                name = "Drug Resistance 2",
                description = "Pathogen resists most medications.",
                cost = 16,
                category = UpgradeCategory.ABILITIES,
                tier = 2,
                prerequisites = listOf("ability_drug_1"),
                drugResistanceBonus = 1
            ),
            Upgrade(
                id = "ability_drug_3",
                name = "Drug Resistance 3",
                description = "Pathogen is nearly immune to all treatments.",
                cost = 28,
                category = UpgradeCategory.ABILITIES,
                tier = 3,
                prerequisites = listOf("ability_drug_2"),
                drugResistanceBonus = 1
            ),

            // ABILITIES - Special
            Upgrade(
                id = "ability_stealth",
                name = "Hardened Resurgence",
                description = "Pathogen is harder to detect. Reduces severity visibility.",
                cost = 15,
                category = UpgradeCategory.ABILITIES,
                tier = 2,
                severityBonus = -0.15f,
                infectivityBonus = 0.10f
            ),
            Upgrade(
                id = "ability_mutation",
                name = "Genetic Hardening",
                description = "Slows cure research by making pathogen harder to analyze.",
                cost = 20,
                category = UpgradeCategory.ABILITIES,
                tier = 2,
                drugResistanceBonus = 1
            ),
            Upgrade(
                id = "ability_genetic_reshuffle_1",
                name = "Genetic Reshuffle 1",
                description = "Randomizes pathogen genetics, setting back cure research.",
                cost = 25,
                category = UpgradeCategory.ABILITIES,
                tier = 3,
                drugResistanceBonus = 1
            ),
            Upgrade(
                id = "ability_genetic_reshuffle_2",
                name = "Genetic Reshuffle 2",
                description = "Drastically alters pathogen, heavily impeding cure development.",
                cost = 35,
                category = UpgradeCategory.ABILITIES,
                tier = 3,
                prerequisites = listOf("ability_genetic_reshuffle_1"),
                drugResistanceBonus = 2
            )
        )
    }

    /**
     * Get upgrades by category
     */
    fun getUpgradesByCategory(category: UpgradeCategory): List<Upgrade> {
        return getAllUpgrades().filter { it.category == category }
    }

    /**
     * Get a specific upgrade by ID
     */
    fun getUpgradeById(id: String): Upgrade? {
        return getAllUpgrades().find { it.id == id }
    }
}

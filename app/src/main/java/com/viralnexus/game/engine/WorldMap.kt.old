package com.viralnexus.game.engine

import kotlin.random.Random

class WorldMap {
    val countries = mutableListOf<Country>()
    
    init {
        initializeCountries()
        establishConnections()
    }
    
    private fun initializeCountries() {
        // Major world countries with realistic data
        countries.addAll(listOf(
            Country("China", 1440000000L, Climate.TEMPERATE, Wealth.MIDDLE, Healthcare(0.7f, 0.8f), 0f, 0f, 35.8617f, 104.1954f),
            Country("India", 1380000000L, Climate.TROPICAL, Wealth.LOW, Healthcare(0.5f, 0.6f), 0f, 0f, 20.5937f, 78.9629f),
            Country("United States", 331000000L, Climate.TEMPERATE, Wealth.HIGH, Healthcare(0.9f, 0.9f), 0f, 0f, 37.0902f, -95.7129f),
            Country("Indonesia", 273000000L, Climate.TROPICAL, Wealth.MIDDLE, Healthcare(0.6f, 0.7f), 0f, 0f, -0.7893f, 113.9213f),
            Country("Pakistan", 220000000L, Climate.ARID, Wealth.LOW, Healthcare(0.4f, 0.5f), 0f, 0f, 30.3753f, 69.3451f),
            Country("Brazil", 212000000L, Climate.TROPICAL, Wealth.MIDDLE, Healthcare(0.6f, 0.7f), 0f, 0f, -14.2350f, -51.9253f),
            Country("Nigeria", 206000000L, Climate.TROPICAL, Wealth.LOW, Healthcare(0.3f, 0.4f), 0f, 0f, 9.0820f, 8.6753f),
            Country("Bangladesh", 164000000L, Climate.TROPICAL, Wealth.LOW, Healthcare(0.4f, 0.5f), 0f, 0f, 23.6850f, 90.3563f),
            Country("Russia", 146000000L, Climate.COLD, Wealth.MIDDLE, Healthcare(0.7f, 0.8f), 0f, 0f, 61.5240f, 105.3188f),
            Country("Mexico", 128000000L, Climate.ARID, Wealth.MIDDLE, Healthcare(0.6f, 0.7f), 0f, 0f, 23.6345f, -102.5528f),
            Country("Japan", 126000000L, Climate.TEMPERATE, Wealth.HIGH, Healthcare(0.95f, 0.95f), 0f, 0f, 36.2048f, 138.2529f),
            Country("Ethiopia", 114000000L, Climate.ARID, Wealth.LOW, Healthcare(0.3f, 0.4f), 0f, 0f, 9.1450f, 40.4897f),
            Country("Philippines", 109000000L, Climate.TROPICAL, Wealth.MIDDLE, Healthcare(0.5f, 0.6f), 0f, 0f, 12.8797f, 121.7740f),
            Country("Egypt", 102000000L, Climate.ARID, Wealth.MIDDLE, Healthcare(0.5f, 0.6f), 0f, 0f, 26.0975f, 31.1309f),
            Country("Vietnam", 97000000L, Climate.TROPICAL, Wealth.MIDDLE, Healthcare(0.6f, 0.7f), 0f, 0f, 14.0583f, 108.2772f),
            Country("Turkey", 84000000L, Climate.TEMPERATE, Wealth.MIDDLE, Healthcare(0.7f, 0.8f), 0f, 0f, 38.9637f, 35.2433f),
            Country("Iran", 83000000L, Climate.ARID, Wealth.MIDDLE, Healthcare(0.6f, 0.7f), 0f, 0f, 32.4279f, 53.6880f),
            Country("Germany", 83000000L, Climate.TEMPERATE, Wealth.HIGH, Healthcare(0.95f, 0.95f), 0f, 0f, 51.1657f, 10.4515f),
            Country("Thailand", 69000000L, Climate.TROPICAL, Wealth.MIDDLE, Healthcare(0.7f, 0.8f), 0f, 0f, 15.8700f, 100.9925f),
            Country("United Kingdom", 67000000L, Climate.TEMPERATE, Wealth.HIGH, Healthcare(0.9f, 0.9f), 0f, 0f, 55.3781f, -3.4360f),
            Country("France", 67000000L, Climate.TEMPERATE, Wealth.HIGH, Healthcare(0.9f, 0.9f), 0f, 0f, 46.6031f, 1.8883f),
            Country("Italy", 60000000L, Climate.TEMPERATE, Wealth.HIGH, Healthcare(0.85f, 0.9f), 0f, 0f, 41.8719f, 12.5674f),
            Country("Tanzania", 59000000L, Climate.TROPICAL, Wealth.LOW, Healthcare(0.3f, 0.4f), 0f, 0f, -6.3690f, 34.8888f),
            Country("South Africa", 59000000L, Climate.TEMPERATE, Wealth.MIDDLE, Healthcare(0.5f, 0.6f), 0f, 0f, -30.5595f, 22.9375f),
            Country("Myanmar", 54000000L, Climate.TROPICAL, Wealth.LOW, Healthcare(0.4f, 0.5f), 0f, 0f, 21.9162f, 95.9560f),
            Country("Kenya", 53000000L, Climate.TROPICAL, Wealth.LOW, Healthcare(0.4f, 0.5f), 0f, 0f, -0.0236f, 37.9062f),
            Country("South Korea", 51000000L, Climate.TEMPERATE, Wealth.HIGH, Healthcare(0.9f, 0.95f), 0f, 0f, 35.9078f, 127.7669f),
            Country("Colombia", 50000000L, Climate.TROPICAL, Wealth.MIDDLE, Healthcare(0.6f, 0.7f), 0f, 0f, 4.5709f, -74.2973f),
            Country("Spain", 47000000L, Climate.TEMPERATE, Wealth.HIGH, Healthcare(0.9f, 0.9f), 0f, 0f, 40.4637f, -3.7492f),
            Country("Uganda", 45000000L, Climate.TROPICAL, Wealth.LOW, Healthcare(0.3f, 0.4f), 0f, 0f, 1.3733f, 32.2903f),
            Country("Argentina", 45000000L, Climate.TEMPERATE, Wealth.MIDDLE, Healthcare(0.7f, 0.8f), 0f, 0f, -38.4161f, -63.6167f),
            Country("Algeria", 43000000L, Climate.ARID, Wealth.MIDDLE, Healthcare(0.6f, 0.7f), 0f, 0f, 28.0339f, 1.6596f),
            Country("Sudan", 43000000L, Climate.ARID, Wealth.LOW, Healthcare(0.3f, 0.4f), 0f, 0f, 12.8628f, 30.2176f),
            Country("Ukraine", 44000000L, Climate.TEMPERATE, Wealth.MIDDLE, Healthcare(0.6f, 0.7f), 0f, 0f, 48.3794f, 31.1656f),
            Country("Iraq", 40000000L, Climate.ARID, Wealth.MIDDLE, Healthcare(0.5f, 0.6f), 0f, 0f, 33.2232f, 43.6793f),
            Country("Afghanistan", 38000000L, Climate.ARID, Wealth.LOW, Healthcare(0.2f, 0.3f), 0f, 0f, 33.9391f, 67.7100f),
            Country("Poland", 38000000L, Climate.TEMPERATE, Wealth.HIGH, Healthcare(0.8f, 0.85f), 0f, 0f, 51.9194f, 19.1451f),
            Country("Canada", 37000000L, Climate.COLD, Wealth.HIGH, Healthcare(0.9f, 0.95f), 0f, 0f, 56.1304f, -106.3468f),
            Country("Morocco", 36000000L, Climate.ARID, Wealth.MIDDLE, Healthcare(0.5f, 0.6f), 0f, 0f, 31.7917f, -7.0926f),
            Country("Saudi Arabia", 34000000L, Climate.ARID, Wealth.HIGH, Healthcare(0.8f, 0.85f), 0f, 0f, 23.8859f, 45.0792f),
            Country("Uzbekistan", 33000000L, Climate.ARID, Wealth.MIDDLE, Healthcare(0.5f, 0.6f), 0f, 0f, 41.3775f, 64.5853f)
        ))
        
        // Set initial infection in China (Patient Zero)
        countries.find { it.name == "China" }?.infected = 1L
    }
    
    private fun establishConnections() {
        // Establish realistic country connections based on:
        // - Geographic proximity
        // - Trade relationships  
        // - Air travel routes
        // - Population movement patterns
        
        val connections = mapOf(
            "China" to listOf("Russia", "India", "Pakistan", "Myanmar", "Vietnam", "Thailand", "Japan", "South Korea"),
            "India" to listOf("China", "Pakistan", "Bangladesh", "Myanmar", "Iran", "Afghanistan"),
            "United States" to listOf("Canada", "Mexico", "United Kingdom", "Germany", "Japan", "South Korea"),
            "Russia" to listOf("China", "Ukraine", "Poland", "Germany", "Iran", "Afghanistan"),
            "Germany" to listOf("France", "Italy", "Poland", "United Kingdom", "Russia", "Turkey"),
            "United Kingdom" to listOf("France", "Germany", "United States", "Canada", "Spain", "Italy"),
            "France" to listOf("Germany", "United Kingdom", "Spain", "Italy", "Algeria", "Morocco"),
            "Brazil" to listOf("Argentina", "Colombia", "United States"),
            "Nigeria" to listOf("Sudan", "Ethiopia", "Kenya", "Algeria"),
            "Japan" to listOf("China", "South Korea", "United States", "Thailand", "Philippines"),
            "South Korea" to listOf("China", "Japan", "United States"),
            "Iran" to listOf("Iraq", "Afghanistan", "Pakistan", "Turkey", "Russia"),
            "Turkey" to listOf("Iran", "Iraq", "Germany", "France", "Italy"),
            "Egypt" to listOf("Sudan", "Saudi Arabia", "Algeria", "Morocco"),
            "Saudi Arabia" to listOf("Iraq", "Iran", "Egypt", "Sudan", "Ethiopia"),
            "South Africa" to listOf("Tanzania", "Kenya", "Nigeria"),
            "Thailand" to listOf("China", "Vietnam", "Myanmar", "Japan", "Philippines"),
            "Vietnam" to listOf("China", "Thailand", "Philippines"),
            "Philippines" to listOf("China", "Japan", "Thailand", "Vietnam", "Indonesia"),
            "Indonesia" to listOf("Philippines", "Thailand", "India"),
            "Bangladesh" to listOf("India", "Myanmar"),
            "Myanmar" to listOf("China", "India", "Bangladesh", "Thailand"),
            "Pakistan" to listOf("China", "India", "Iran", "Afghanistan"),
            "Afghanistan" to listOf("Pakistan", "Iran", "Russia"),
            "Ethiopia" to listOf("Sudan", "Kenya", "Tanzania", "Saudi Arabia"),
            "Kenya" to listOf("Ethiopia", "Tanzania", "Sudan", "Uganda", "South Africa"),
            "Tanzania" to listOf("Kenya", "Uganda", "Ethiopia", "South Africa"),
            "Uganda" to listOf("Kenya", "Tanzania", "Sudan", "Ethiopia"),
            "Sudan" to listOf("Ethiopia", "Kenya", "Uganda", "Egypt", "Algeria"),
            "Algeria" to listOf("Morocco", "France", "Sudan", "Nigeria", "Egypt"),
            "Morocco" to listOf("Algeria", "France", "Spain", "Egypt"),
            "Spain" to listOf("France", "Morocco", "Italy", "United Kingdom"),
            "Italy" to listOf("France", "Germany", "Spain", "Turkey", "United Kingdom"),
            "Poland" to listOf("Germany", "Russia", "Ukraine"),
            "Ukraine" to listOf("Russia", "Poland", "Germany", "Turkey"),
            "Canada" to listOf("United States", "United Kingdom"),
            "Mexico" to listOf("United States", "Colombia"),
            "Colombia" to listOf("Brazil", "Mexico", "Argentina"),
            "Argentina" to listOf("Brazil", "Colombia"),
            "Iraq" to listOf("Iran", "Turkey", "Saudi Arabia"),
            "Uzbekistan" to listOf("Russia", "Afghanistan", "Iran")
        )
        
        // Create bidirectional connections
        for ((countryName, neighborNames) in connections) {
            val country = countries.find { it.name == countryName }
            if (country != null) {
                for (neighborName in neighborNames) {
                    val neighbor = countries.find { it.name == neighborName }
                    if (neighbor != null && !country.neighbors.contains(neighbor)) {
                        country.neighbors.add(neighbor)
                        neighbor.neighbors.add(country)
                    }
                }
            }
        }
    }
}

data class Country(
    val name: String,
    val population: Long,
    val climate: Climate,
    val wealth: Wealth,
    val healthcare: Healthcare,
    var responseLevel: Float,
    var researchProgress: Float,
    val latitude: Float,
    val longitude: Float,
    var infected: Long = 0L,
    var dead: Long = 0L,
    val neighbors: MutableList<Country> = mutableListOf()
) {
    fun connectivityTo(other: Country): Float {
        // Base connectivity
        var connectivity = 0.1f
        
        // Distance factor (closer countries have higher connectivity)
        val distance = calculateDistance(this.latitude, this.longitude, other.latitude, other.longitude)
        connectivity *= maxOf(0.1f, 1f - distance / 20000f) // 20000km max distance
        
        // Wealth factor (richer countries have more travel)
        connectivity *= (this.wealth.connectivityMultiplier + other.wealth.connectivityMultiplier) / 2f
        
        // Population factor
        connectivity *= (1f + (this.population + other.population) / 2000000000f)
        
        return connectivity
    }
    
    private fun calculateDistance(lat1: Float, lon1: Float, lat2: Float, lon2: Float): Float {
        val earthRadius = 6371000f // meters
        val dLat = Math.toRadians((lat2 - lat1).toDouble())
        val dLon = Math.toRadians((lon2 - lon1).toDouble())
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1.toDouble())) * 
                kotlin.math.cos(Math.toRadians(lat2.toDouble())) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return (earthRadius * c).toFloat()
    }
}

data class Healthcare(
    var effectiveness: Float,
    var effectivenessMultiplier: Float
)

enum class Climate(val infectivityMultiplier: Float) {
    TROPICAL(1.3f),    // Hot, humid - good for pathogens
    TEMPERATE(1.0f),   // Moderate conditions
    ARID(0.8f),        // Hot, dry - harder for pathogens
    COLD(0.7f)         // Cold - some pathogens struggle
}

enum class Wealth(val resistanceMultiplier: Float, val connectivityMultiplier: Float) {
    LOW(1.2f, 0.5f),      // Poor healthcare, low travel
    MIDDLE(1.0f, 1.0f),   // Average conditions
    HIGH(0.7f, 1.5f)      // Good healthcare, high travel
}
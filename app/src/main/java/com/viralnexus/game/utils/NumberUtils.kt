package com.viralnexus.game.utils

/**
 * Utility functions for number formatting
 */

/**
 * Format large numbers with K/M/B suffixes
 * @param number The number to format
 * @return Formatted string (e.g., "1.2M", "3.5K", "45")
 */
fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000_000 -> String.format("%.1fB", number / 1_000_000_000.0)
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

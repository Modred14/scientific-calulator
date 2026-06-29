package com.university.scientificcalculator.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * CalculationHistory
 *
 * Data model representing a single calculation entry in the history log.
 * Stores the expression, result, type, and timestamp.
 */
data class CalculationHistory(
    val id: Long = System.currentTimeMillis(),          // Unique ID (timestamp-based)
    val expression: String,                              // The input expression (e.g. "sin(30)")
    val result: String,                                  // The computed result (e.g. "0.5")
    val type: CalculationType = CalculationType.BASIC,  // Category of calculation
    val timestamp: Long = System.currentTimeMillis()    // When this calculation was made
) {
    /**
     * Returns the timestamp formatted as "HH:mm:ss" for display in history.
     */
    fun formattedTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Returns a single-line summary: "expression = result"
     */
    fun summary(): String = "$expression = $result"
}

/**
 * Types of calculations supported by the app.
 * Used for filtering and displaying history by category.
 */
enum class CalculationType {
    BASIC,       // +, -, *, /
    SCIENTIFIC,  // trig, log, factorial, etc.
    MATRIX,      // Matrix operations
    STATISTICS   // Mean, median, mode, std dev
}

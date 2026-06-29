package com.university.scientificcalculator.utils

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * StatisticsEngine
 *
 * Provides statistical calculations: Mean, Median, Mode, and Standard Deviation.
 * All functions accept a List<Double> and return a formatted string result.
 */
object StatisticsEngine {

    // ─── Core Statistics ──────────────────────────────────────────────────────

    /**
     * Mean (arithmetic average): sum of all values divided by the count.
     * Formula: μ = (Σx) / n
     */
    fun mean(data: List<Double>): Double {
        require(data.isNotEmpty()) { "Cannot calculate mean of an empty dataset" }
        return data.sum() / data.size
    }

    /**
     * Median: the middle value when the dataset is sorted.
     * If even number of values, the median is the average of the two middle values.
     */
    fun median(data: List<Double>): Double {
        require(data.isNotEmpty()) { "Cannot calculate median of an empty dataset" }
        val sorted = data.sorted()
        val n = sorted.size
        return if (n % 2 == 0) {
            (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0
        } else {
            sorted[n / 2]
        }
    }

    /**
     * Mode: the value(s) that appear most frequently.
     * Returns all modes if there are multiple values with the same highest frequency.
     * Returns "No mode" if all values are unique.
     */
    fun mode(data: List<Double>): String {
        require(data.isNotEmpty()) { "Cannot calculate mode of an empty dataset" }

        // Count frequency of each value
        val frequency = mutableMapOf<Double, Int>()
        for (value in data) {
            frequency[value] = frequency.getOrDefault(value, 0) + 1
        }

        val maxFrequency = frequency.values.max()!!

        // If all values appear only once, there is no mode
        if (maxFrequency == 1) return "No mode (all values unique)"

        // Collect all values with the maximum frequency
        val modes = frequency.entries
            .filter { it.value == maxFrequency }
            .map { it.key }
            .sorted()

        val formattedModes = modes.joinToString(", ") { CalculatorEngine.formatResult(it) }
        return "$formattedModes (frequency: $maxFrequency)"
    }

    /**
     * Population Standard Deviation: measures the spread of data around the mean.
     * Formula: σ = sqrt( Σ(x - μ)² / n )
     *
     * Uses population std dev (divides by n, not n-1) as it is the most common
     * default in introductory statistics courses.
     */
    fun standardDeviation(data: List<Double>): Double {
        require(data.size >= 2) { "Standard deviation requires at least 2 values" }
        val avg = mean(data)
        val variance = data.sumOf { (it - avg).pow(2) } / data.size
        return sqrt(variance)
    }

    /**
     * Sample Standard Deviation (uses n-1 denominator / Bessel's correction).
     * Formula: s = sqrt( Σ(x - x̄)² / (n-1) )
     */
    fun sampleStandardDeviation(data: List<Double>): Double {
        require(data.size >= 2) { "Sample standard deviation requires at least 2 values" }
        val avg = mean(data)
        val variance = data.sumOf { (it - avg).pow(2) } / (data.size - 1)
        return sqrt(variance)
    }

    /**
     * Returns a complete summary of all statistical measures.
     */
    fun fullSummary(data: List<Double>): StatsSummary {
        return StatsSummary(
            count = data.size,
            mean = mean(data),
            median = median(data),
            mode = mode(data),
            stdDev = standardDeviation(data),
            sampleStdDev = if (data.size >= 2) sampleStandardDeviation(data) else 0.0,
            min = data.min()!!,
            max = data.max()!!,
            range = data.max()!! - data.min()!!,
            sum = data.sum()
        )
    }

    /**
     * Parses a comma or space-separated string of numbers into a List<Double>.
     * Throws IllegalArgumentException if parsing fails.
     */
    fun parseDataset(input: String): List<Double> {
        val cleaned = input.trim()
        if (cleaned.isEmpty()) throw IllegalArgumentException("No data entered")

        // Split on commas, spaces, semicolons, or newlines
        val parts = cleaned.split(Regex("[,;\\s]+")).filter { it.isNotEmpty() }

        if (parts.isEmpty()) throw IllegalArgumentException("No valid numbers found")

        return parts.mapIndexed { index, part ->
            part.toDoubleOrNull()
                ?: throw IllegalArgumentException("Invalid number '${part}' at position ${index + 1}")
        }
    }
}

/**
 * Data class holding all statistical results for a dataset.
 */
data class StatsSummary(
    val count: Int,
    val mean: Double,
    val median: Double,
    val mode: String,
    val stdDev: Double,
    val sampleStdDev: Double,
    val min: Double,
    val max: Double,
    val range: Double,
    val sum: Double
)

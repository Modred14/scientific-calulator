package com.university.scientificcalculator

import com.university.scientificcalculator.utils.StatisticsEngine
import org.junit.Assert.*
import org.junit.Test

/**
 * StatisticsEngineTest
 * Unit tests for all statistical functions.
 */
class StatisticsEngineTest {

    private val DELTA = 1e-6

    private val data = listOf(4.0, 7.0, 2.0, 9.0, 4.0, 1.0, 8.0, 4.0, 3.0, 6.0)

    // ─── Mean ─────────────────────────────────────────────────────────────────

    @Test fun testMean() {
        // (4+7+2+9+4+1+8+4+3+6) / 10 = 48 / 10 = 4.8
        assertEquals(4.8, StatisticsEngine.mean(data), DELTA)
    }

    @Test fun testMeanSingleValue() {
        assertEquals(5.0, StatisticsEngine.mean(listOf(5.0)), DELTA)
    }

    @Test fun testMeanEmpty() {
        try {
            StatisticsEngine.mean(emptyList())
            fail("Should throw for empty dataset")
        } catch (e: IllegalArgumentException) { /* expected */ }
    }

    // ─── Median ───────────────────────────────────────────────────────────────

    @Test fun testMedianEvenCount() {
        // sorted: [1,2,3,4,4,4,6,7,8,9] → median = (4+4)/2 = 4.0
        assertEquals(4.0, StatisticsEngine.median(data), DELTA)
    }

    @Test fun testMedianOddCount() {
        // sorted: [1,3,5] → median = 3
        assertEquals(3.0, StatisticsEngine.median(listOf(5.0, 1.0, 3.0)), DELTA)
    }

    // ─── Mode ─────────────────────────────────────────────────────────────────

    @Test fun testMode() {
        // 4 appears 3 times
        val modeResult = StatisticsEngine.mode(data)
        assertTrue("Mode should contain 4", modeResult.contains("4"))
        assertTrue("Mode should show frequency 3", modeResult.contains("3"))
    }

    @Test fun testModeNoMode() {
        val result = StatisticsEngine.mode(listOf(1.0, 2.0, 3.0))
        assertTrue(result.contains("No mode"))
    }

    // ─── Standard Deviation ──────────────────────────────────────────────────

    @Test fun testStdDev() {
        // Known value for this dataset
        val std = StatisticsEngine.standardDeviation(data)
        assertTrue("Std dev should be positive", std > 0)
        assertEquals(2.317, std, 0.001)   // pre-computed
    }

    @Test fun testSampleStdDev() {
        val s = StatisticsEngine.sampleStandardDeviation(data)
        assertTrue("Sample std dev should be > population std dev", s > StatisticsEngine.standardDeviation(data))
    }

    @Test fun testStdDevRequiresTwoValues() {
        try {
            StatisticsEngine.standardDeviation(listOf(5.0))
            fail("Should require at least 2 values")
        } catch (e: IllegalArgumentException) { /* expected */ }
    }

    // ─── Parse Dataset ────────────────────────────────────────────────────────

    @Test fun testParseCommaDelimited() {
        val result = StatisticsEngine.parseDataset("1, 2, 3, 4, 5")
        assertEquals(5, result.size)
        assertEquals(1.0, result[0], DELTA)
        assertEquals(5.0, result[4], DELTA)
    }

    @Test fun testParseSpaceDelimited() {
        val result = StatisticsEngine.parseDataset("10 20 30")
        assertEquals(3, result.size)
    }

    @Test fun testParseInvalidThrows() {
        try {
            StatisticsEngine.parseDataset("1, abc, 3")
            fail("Should throw for invalid number")
        } catch (e: IllegalArgumentException) { /* expected */ }
    }

    @Test fun testParseEmptyThrows() {
        try {
            StatisticsEngine.parseDataset("   ")
            fail("Should throw for empty input")
        } catch (e: IllegalArgumentException) { /* expected */ }
    }

    // ─── Full Summary ─────────────────────────────────────────────────────────

    @Test fun testFullSummary() {
        val s = StatisticsEngine.fullSummary(data)
        assertEquals(10, s.count)
        assertEquals(4.8, s.mean, DELTA)
        assertEquals(48.0, s.sum, DELTA)
        assertEquals(1.0, s.min, DELTA)
        assertEquals(9.0, s.max, DELTA)
        assertEquals(8.0, s.range, DELTA)
    }
}

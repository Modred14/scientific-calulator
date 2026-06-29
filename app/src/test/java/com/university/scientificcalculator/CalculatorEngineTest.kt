package com.university.scientificcalculator

import com.university.scientificcalculator.utils.CalculatorEngine
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

/**
 * CalculatorEngineTest
 *
 * Unit tests for CalculatorEngine. Run these with:
 *   ./gradlew test
 * or in Android Studio: right-click the file → Run Tests
 */
class CalculatorEngineTest {

    private val DELTA = 1e-9   // Tolerance for floating-point comparisons

    // ─── Basic Arithmetic ─────────────────────────────────────────────────────

    @Test fun testAddition() {
        assertEquals("8", CalculatorEngine.evaluate("3 + 5"))
    }

    @Test fun testSubtraction() {
        assertEquals("1", CalculatorEngine.evaluate("6 - 5"))
    }

    @Test fun testMultiplication() {
        assertEquals("15", CalculatorEngine.evaluate("3 * 5"))
    }

    @Test fun testDivision() {
        assertEquals("4", CalculatorEngine.evaluate("20 / 5"))
    }

    @Test fun testDivisionByZero() {
        val result = CalculatorEngine.evaluate("5 / 0")
        assertTrue("Division by zero should return error", result.startsWith("Math Error"))
    }

    @Test fun testChainedOperations() {
        // 2 + 3 * 4 = 2 + 12 = 14 (precedence respected)
        assertEquals("14", CalculatorEngine.evaluate("2 + 3 * 4"))
    }

    @Test fun testParentheses() {
        assertEquals("20", CalculatorEngine.evaluate("(2 + 3) * 4"))
    }

    @Test fun testNegativeNumbers() {
        assertEquals("-3", CalculatorEngine.evaluate("-5 + 2"))
    }

    @Test fun testDecimalArithmetic() {
        val result = CalculatorEngine.evaluate("1.5 + 2.5")
        assertEquals("4", result)
    }

    // ─── Scientific Functions ─────────────────────────────────────────────────

    @Test fun testSin90() {
        val result = CalculatorEngine.sin(90.0)
        assertEquals(1.0, result, DELTA)
    }

    @Test fun testSin0() {
        val result = CalculatorEngine.sin(0.0)
        assertEquals(0.0, result, DELTA)
    }

    @Test fun testCos0() {
        val result = CalculatorEngine.cos(0.0)
        assertEquals(1.0, result, DELTA)
    }

    @Test fun testCos90() {
        val result = CalculatorEngine.cos(90.0)
        assertTrue(abs(result) < DELTA)   // cos(90°) = 0
    }

    @Test fun testTan45() {
        val result = CalculatorEngine.tan(45.0)
        assertEquals(1.0, result, DELTA)
    }

    @Test fun testSqrt() {
        assertEquals(4.0, CalculatorEngine.sqrt(16.0), DELTA)
    }

    @Test fun testSqrtNegative() {
        try {
            CalculatorEngine.sqrt(-1.0)
            fail("Should throw ArithmeticException for sqrt of negative")
        } catch (e: ArithmeticException) {
            // expected
        }
    }

    @Test fun testLog10() {
        assertEquals(2.0, CalculatorEngine.log10(100.0), DELTA)
    }

    @Test fun testLn() {
        assertEquals(1.0, CalculatorEngine.ln(Math.E), DELTA)
    }

    @Test fun testFactorial5() {
        assertEquals(120.0, CalculatorEngine.factorial(5.0), DELTA)
    }

    @Test fun testFactorial0() {
        assertEquals(1.0, CalculatorEngine.factorial(0.0), DELTA)
    }

    @Test fun testFactorialNegative() {
        try {
            CalculatorEngine.factorial(-1.0)
            fail("Should throw ArithmeticException for negative factorial")
        } catch (e: ArithmeticException) {
            // expected
        }
    }

    @Test fun testPower() {
        assertEquals(8.0, CalculatorEngine.power(2.0, 3.0), DELTA)
    }

    @Test fun testPercentage() {
        assertEquals(0.5, CalculatorEngine.percentage(50.0), DELTA)
    }

    @Test fun testPermutation() {
        // 5P2 = 5! / (5-2)! = 120 / 6 = 20
        assertEquals(20.0, CalculatorEngine.permutation(5.0, 2.0), DELTA)
    }

    @Test fun testCombination() {
        // 5C2 = 5! / (2! * 3!) = 120 / 12 = 10
        assertEquals(10.0, CalculatorEngine.combination(5.0, 2.0), DELTA)
    }

    // ─── Hyperbolic Functions ─────────────────────────────────────────────────

    @Test fun testSinh0() {
        assertEquals(0.0, CalculatorEngine.sinh(0.0), DELTA)
    }

    @Test fun testCosh0() {
        assertEquals(1.0, CalculatorEngine.cosh(0.0), DELTA)
    }

    @Test fun testTanh0() {
        assertEquals(0.0, CalculatorEngine.tanh(0.0), DELTA)
    }

    // ─── Format Result ────────────────────────────────────────────────────────

    @Test fun testFormatWholeNumber() {
        assertEquals("5", CalculatorEngine.formatResult(5.0))
    }

    @Test fun testFormatDecimal() {
        assertEquals("3.14159", CalculatorEngine.formatResult(3.14159))
    }

    @Test fun testFormatNaN() {
        assertEquals("Not a Number", CalculatorEngine.formatResult(Double.NaN))
    }

    @Test fun testFormatInfinity() {
        assertEquals("Infinity", CalculatorEngine.formatResult(Double.POSITIVE_INFINITY))
    }
}

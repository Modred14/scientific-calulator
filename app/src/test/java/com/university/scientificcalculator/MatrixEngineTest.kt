package com.university.scientificcalculator

import com.university.scientificcalculator.utils.MatrixEngine
import org.junit.Assert.*
import org.junit.Test

/**
 * MatrixEngineTest
 * Unit tests for all matrix operations.
 */
class MatrixEngineTest {

    private val DELTA = 1e-9

    // Helper to create 2×2 identity matrix
    private fun identity2() = arrayOf(doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0))

    // ─── Addition ─────────────────────────────────────────────────────────────

    @Test fun testAddition2x2() {
        val a = arrayOf(doubleArrayOf(1.0, 2.0), doubleArrayOf(3.0, 4.0))
        val b = arrayOf(doubleArrayOf(5.0, 6.0), doubleArrayOf(7.0, 8.0))
        val result = MatrixEngine.add(a, b)
        assertEquals(6.0,  result[0][0], DELTA)
        assertEquals(8.0,  result[0][1], DELTA)
        assertEquals(10.0, result[1][0], DELTA)
        assertEquals(12.0, result[1][1], DELTA)
    }

    // ─── Subtraction ──────────────────────────────────────────────────────────

    @Test fun testSubtraction2x2() {
        val a = arrayOf(doubleArrayOf(5.0, 6.0), doubleArrayOf(7.0, 8.0))
        val b = arrayOf(doubleArrayOf(1.0, 2.0), doubleArrayOf(3.0, 4.0))
        val result = MatrixEngine.subtract(a, b)
        assertEquals(4.0, result[0][0], DELTA)
        assertEquals(4.0, result[0][1], DELTA)
        assertEquals(4.0, result[1][0], DELTA)
        assertEquals(4.0, result[1][1], DELTA)
    }

    // ─── Multiplication ───────────────────────────────────────────────────────

    @Test fun testMultiplicationByIdentity() {
        val a = arrayOf(doubleArrayOf(3.0, 4.0), doubleArrayOf(5.0, 6.0))
        val id = identity2()
        val result = MatrixEngine.multiply(a, id)
        assertEquals(3.0, result[0][0], DELTA)
        assertEquals(4.0, result[0][1], DELTA)
        assertEquals(5.0, result[1][0], DELTA)
        assertEquals(6.0, result[1][1], DELTA)
    }

    @Test fun testMultiplication2x2() {
        val a = arrayOf(doubleArrayOf(1.0, 2.0), doubleArrayOf(3.0, 4.0))
        val b = arrayOf(doubleArrayOf(2.0, 0.0), doubleArrayOf(1.0, 2.0))
        val result = MatrixEngine.multiply(a, b)
        // [1*2+2*1, 1*0+2*2] = [4, 4]
        // [3*2+4*1, 3*0+4*2] = [10, 8]
        assertEquals(4.0,  result[0][0], DELTA)
        assertEquals(4.0,  result[0][1], DELTA)
        assertEquals(10.0, result[1][0], DELTA)
        assertEquals(8.0,  result[1][1], DELTA)
    }

    // ─── Determinant ──────────────────────────────────────────────────────────

    @Test fun testDeterminant2x2() {
        val m = arrayOf(doubleArrayOf(3.0, 8.0), doubleArrayOf(4.0, 6.0))
        // det = 3*6 - 8*4 = 18 - 32 = -14
        assertEquals(-14.0, MatrixEngine.determinant(m), DELTA)
    }

    @Test fun testDeterminant2x2Identity() {
        assertEquals(1.0, MatrixEngine.determinant(identity2()), DELTA)
    }

    @Test fun testDeterminant3x3() {
        val m = arrayOf(
            doubleArrayOf(6.0, 1.0, 1.0),
            doubleArrayOf(4.0, -2.0, 5.0),
            doubleArrayOf(2.0, 8.0, 7.0)
        )
        // det = 6*((-2)*7 - 5*8) - 1*(4*7 - 5*2) + 1*(4*8 - (-2)*2)
        //     = 6*(-14-40) - 1*(28-10) + 1*(32+4)
        //     = 6*(-54) - 18 + 36 = -324 - 18 + 36 = -306
        assertEquals(-306.0, MatrixEngine.determinant(m), DELTA)
    }

    @Test fun testDeterminantSingular() {
        // Row 2 = 2 * Row 1 → determinant must be 0
        val m = arrayOf(
            doubleArrayOf(1.0, 2.0),
            doubleArrayOf(2.0, 4.0)
        )
        assertEquals(0.0, MatrixEngine.determinant(m), DELTA)
    }

    // ─── Parse Matrix ────────────────────────────────────────────────────────

    @Test fun testParseMatrix() {
        val values = listOf("1", "2", "3", "4")
        val m = MatrixEngine.parseMatrix(values, 2)
        assertEquals(1.0, m[0][0], DELTA)
        assertEquals(2.0, m[0][1], DELTA)
        assertEquals(3.0, m[1][0], DELTA)
        assertEquals(4.0, m[1][1], DELTA)
    }

    @Test fun testParseMatrixEmptyAsZero() {
        val values = listOf("", "2", "3", "")
        val m = MatrixEngine.parseMatrix(values, 2)
        assertEquals(0.0, m[0][0], DELTA)
        assertEquals(0.0, m[1][1], DELTA)
    }
}

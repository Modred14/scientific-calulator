package com.university.scientificcalculator.utils

/**
 * MatrixEngine
 *
 * Utility object that handles matrix operations for 2×2, 3×3, and 4×4 matrices.
 * Operations: Addition, Subtraction, Multiplication, Determinant.
 *
 * All matrices are represented as Array<DoubleArray> (row-major).
 */
object MatrixEngine {

    // ─── Validation ──────────────────────────────────────────────────────────

    /**
     * Validates that a matrix is square (n×n) with size 2, 3, or 4.
     */
    fun validateMatrix(matrix: Array<DoubleArray>): Boolean {
        val n = matrix.size
        if (n !in 2..4) return false
        return matrix.all { it.size == n }
    }

    /**
     * Validates that two matrices have the same dimensions.
     */
    fun areSameDimension(a: Array<DoubleArray>, b: Array<DoubleArray>): Boolean {
        if (a.size != b.size) return false
        for (i in a.indices) if (a[i].size != b[i].size) return false
        return true
    }

    // ─── Matrix Operations ────────────────────────────────────────────────────

    /**
     * Adds two matrices of the same dimensions.
     * Result[i][j] = A[i][j] + B[i][j]
     */
    fun add(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        require(areSameDimension(a, b)) { "Matrices must have the same dimensions for addition" }
        val n = a.size
        val m = a[0].size
        return Array(n) { i -> DoubleArray(m) { j -> a[i][j] + b[i][j] } }
    }

    /**
     * Subtracts matrix B from matrix A.
     * Result[i][j] = A[i][j] - B[i][j]
     */
    fun subtract(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        require(areSameDimension(a, b)) { "Matrices must have the same dimensions for subtraction" }
        val n = a.size
        val m = a[0].size
        return Array(n) { i -> DoubleArray(m) { j -> a[i][j] - b[i][j] } }
    }

    /**
     * Multiplies two matrices (standard matrix multiplication).
     * Requires: A is m×k, B is k×n → Result is m×n.
     */
    fun multiply(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        val m = a.size
        val k = a[0].size
        require(k == b.size) { "Matrix dimensions incompatible for multiplication: A columns (${k}) ≠ B rows (${b.size})" }
        val n = b[0].size
        return Array(m) { i ->
            DoubleArray(n) { j ->
                (0 until k).sumOf { p -> a[i][p] * b[p][j] }
            }
        }
    }

    /**
     * Computes the determinant of a square matrix using cofactor expansion.
     * Supports 2×2, 3×3, and 4×4 matrices.
     */
    fun determinant(matrix: Array<DoubleArray>): Double {
        val n = matrix.size
        require(n == matrix[0].size) { "Matrix must be square to compute determinant" }

        return when (n) {
            1 -> matrix[0][0]
            2 -> det2x2(matrix)
            3 -> det3x3(matrix)
            else -> detNxN(matrix)  // General case for 4×4 (and larger)
        }
    }

    // ─── Determinant Helpers ──────────────────────────────────────────────────

    private fun det2x2(m: Array<DoubleArray>): Double =
        m[0][0] * m[1][1] - m[0][1] * m[1][0]

    private fun det3x3(m: Array<DoubleArray>): Double =
        m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1]) -
        m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0]) +
        m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0])

    /**
     * Computes determinant via cofactor expansion along the first row.
     * Works for any square matrix (used for 4×4 here).
     */
    private fun detNxN(m: Array<DoubleArray>): Double {
        val n = m.size
        var det = 0.0
        for (col in 0 until n) {
            val minor = getMinor(m, 0, col)
            val cofactor = (-1.0).pow(col) * determinant(minor)
            det += m[0][col] * cofactor
        }
        return det
    }

    /**
     * Returns the minor matrix obtained by deleting row [row] and column [col].
     */
    private fun getMinor(m: Array<DoubleArray>, row: Int, col: Int): Array<DoubleArray> {
        val n = m.size
        return Array(n - 1) { i ->
            val srcRow = if (i >= row) i + 1 else i
            DoubleArray(n - 1) { j ->
                val srcCol = if (j >= col) j + 1 else j
                m[srcRow][srcCol]
            }
        }
    }

    private fun Double.pow(n: Int): Double {
        var result = 1.0
        repeat(n) { result *= this }
        return result
    }

    // ─── Formatting ───────────────────────────────────────────────────────────

    /**
     * Formats a matrix result as a human-readable string grid.
     */
    fun formatMatrix(matrix: Array<DoubleArray>): String {
        val sb = StringBuilder()
        for (row in matrix) {
            sb.append("[ ")
            sb.append(row.joinToString("  ") { formatValue(it) })
            sb.append(" ]\n")
        }
        return sb.toString().trimEnd()
    }

    /**
     * Formats a single matrix cell value: whole numbers without decimal,
     * others to 4 decimal places.
     */
    fun formatValue(v: Double): String {
        return if (v == kotlin.math.floor(v) && !v.isInfinite()) {
            v.toLong().toString()
        } else {
            "%.4f".format(v).trimEnd('0').trimEnd('.')
        }
    }

    /**
     * Parses a list of string values (from EditText inputs) into an n×n matrix.
     * Throws NumberFormatException if any value is invalid.
     */
    fun parseMatrix(values: List<String>, size: Int): Array<DoubleArray> {
        require(values.size == size * size) { "Expected ${size * size} values for ${size}×${size} matrix" }
        return Array(size) { i ->
            DoubleArray(size) { j ->
                val str = values[i * size + j].trim()
                if (str.isEmpty()) 0.0 else str.toDouble()
            }
        }
    }
}

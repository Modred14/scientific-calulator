package com.university.scientificcalculator.utils

import kotlin.math.*

/**
 * CalculatorEngine
 *
 * Core utility class that handles all mathematical operations for the
 * Scientific Calculator. This is a stateless singleton object that
 * provides pure functions for evaluation and computation.
 *
 * Covers: basic arithmetic, trigonometry, hyperbolic, logarithms,
 * factorial, power, percentage, permutations, combinations.
 */
object CalculatorEngine {

    // ─── Constants ───────────────────────────────────────────────────────────

    private const val MAX_FACTORIAL = 20   // 21! overflows Long
    private const val MAX_DISPLAY_DIGITS = 12

    // ─── Expression Evaluator ────────────────────────────────────────────────

    /**
     * Evaluates a mathematical expression string and returns the result.
     * Supports: +, -, *, /, parentheses, decimal numbers, and negative numbers.
     *
     * @param expression The string expression to evaluate (e.g. "3 + 5 * 2")
     * @return Result as a formatted string, or an error message.
     */
    fun evaluate(expression: String): String {
        return try {
            val cleaned = expression
                .replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-")
                .trim()

            if (cleaned.isEmpty()) return "0"

            val result = ExpressionParser(cleaned).parse()
            formatResult(result)
        } catch (e: ArithmeticException) {
            "Math Error: ${e.message}"
        } catch (e: Exception) {
            "Syntax Error"
        }
    }

    /**
     * Formats a Double result for display:
     * - If the result is a whole number, shows it without decimals.
     * - Otherwise shows up to MAX_DISPLAY_DIGITS significant digits.
     */
    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Not a Number"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"

        return if (value == kotlin.math.floor(value) && !value.isInfinite() && abs(value) < 1e15) {
            value.toLong().toString()
        } else {
            val formatted = "%.${MAX_DISPLAY_DIGITS}g".format(value)
            // Remove trailing zeros after decimal
            if (formatted.contains('.')) {
                formatted.trimEnd('0').trimEnd('.')
            } else {
                formatted
            }
        }
    }

    // ─── Scientific Functions ─────────────────────────────────────────────────

    fun sin(degrees: Double): Double = kotlin.math.sin(Math.toRadians(degrees))
    fun cos(degrees: Double): Double = kotlin.math.cos(Math.toRadians(degrees))
    fun tan(degrees: Double): Double {
        val rad = Math.toRadians(degrees)
        // Protect against undefined values (90°, 270°, etc.)
        if (abs(kotlin.math.cos(rad)) < 1e-10)
            throw ArithmeticException("tan is undefined at ${degrees}°")
        return kotlin.math.tan(rad)
    }

    fun sinh(x: Double): Double = kotlin.math.sinh(x)
    fun cosh(x: Double): Double = kotlin.math.cosh(x)
    fun tanh(x: Double): Double = kotlin.math.tanh(x)

    fun sqrt(x: Double): Double {
        if (x < 0) throw ArithmeticException("Square root of negative number")
        return kotlin.math.sqrt(x)
    }

    fun power(base: Double, exp: Double): Double = base.pow(exp)

    fun log10(x: Double): Double {
        if (x <= 0) throw ArithmeticException("Logarithm of non-positive number")
        return kotlin.math.log10(x)
    }

    fun ln(x: Double): Double {
        if (x <= 0) throw ArithmeticException("Natural log of non-positive number")
        return kotlin.math.ln(x)
    }

    fun percentage(x: Double): Double = x / 100.0

    /**
     * Computes n! (factorial).
     * Only valid for non-negative integers up to MAX_FACTORIAL.
     */
    fun factorial(n: Double): Double {
        val ni = n.toInt()
        if (n < 0 || n != ni.toDouble()) throw ArithmeticException("Factorial requires a non-negative integer")
        if (ni > MAX_FACTORIAL) throw ArithmeticException("Number too large for factorial (max $MAX_FACTORIAL)")
        return (1..ni).fold(1L) { acc, i -> acc * i }.toDouble()
    }

    /**
     * Permutations: nPr = n! / (n - r)!
     */
    fun permutation(n: Double, r: Double): Double {
        val ni = n.toInt(); val ri = r.toInt()
        if (ni < 0 || ri < 0 || ri > ni) throw ArithmeticException("Invalid nPr input")
        return factorial(n) / factorial((ni - ri).toDouble())
    }

    /**
     * Combinations: nCr = n! / (r! * (n - r)!)
     */
    fun combination(n: Double, r: Double): Double {
        val ni = n.toInt(); val ri = r.toInt()
        if (ni < 0 || ri < 0 || ri > ni) throw ArithmeticException("Invalid nCr input")
        return factorial(n) / (factorial(r) * factorial((ni - ri).toDouble()))
    }

    // ─── Inner Expression Parser (Recursive Descent) ─────────────────────────

    /**
     * Recursive descent parser that evaluates arithmetic expressions
     * respecting operator precedence: +/- then * /.
     *
     * Grammar:
     *   expr   := term (('+' | '-') term)*
     *   term   := factor (('*' | '/') factor)*
     *   factor := '-' factor | '(' expr ')' | number
     */
    private class ExpressionParser(private val input: String) {

        private var pos = 0

        fun parse(): Double {
            val result = parseExpression()
            if (pos < input.length) throw IllegalArgumentException("Unexpected character at pos $pos")
            return result
        }

        private fun parseExpression(): Double {
            var result = parseTerm()
            while (pos < input.length) {
                skipWhitespace()
                when {
                    peek() == '+' -> { pos++; result += parseTerm() }
                    peek() == '-' -> { pos++; result -= parseTerm() }
                    else -> break
                }
            }
            return result
        }

        private fun parseTerm(): Double {
            var result = parseFactor()
            while (pos < input.length) {
                skipWhitespace()
                when {
                    peek() == '*' -> { pos++; result *= parseFactor() }
                    peek() == '/' -> {
                        pos++
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Division by zero")
                        result /= divisor
                    }
                    else -> break
                }
            }
            return result
        }

        private fun parseFactor(): Double {
            skipWhitespace()
            // Unary minus
            if (pos < input.length && peek() == '-') {
                pos++
                return -parseFactor()
            }
            // Unary plus
            if (pos < input.length && peek() == '+') {
                pos++
                return parseFactor()
            }
            // Parentheses
            if (pos < input.length && peek() == '(') {
                pos++ // consume '('
                val result = parseExpression()
                skipWhitespace()
                if (pos >= input.length || peek() != ')') throw IllegalArgumentException("Missing closing parenthesis")
                pos++ // consume ')'
                return result
            }
            // Number
            return parseNumber()
        }

        private fun parseNumber(): Double {
            skipWhitespace()
            val start = pos
            if (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) {
                while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) pos++
                // Scientific notation: e.g. 1.5e10
                if (pos < input.length && (input[pos] == 'e' || input[pos] == 'E')) {
                    pos++
                    if (pos < input.length && (input[pos] == '+' || input[pos] == '-')) pos++
                    while (pos < input.length && input[pos].isDigit()) pos++
                }
                return input.substring(start, pos).toDouble()
            }
            throw IllegalArgumentException("Expected number at position $pos, found: '${input.getOrNull(pos)}'")
        }

        private fun peek(): Char = input[pos]

        private fun skipWhitespace() {
            while (pos < input.length && input[pos] == ' ') pos++
        }
    }
}

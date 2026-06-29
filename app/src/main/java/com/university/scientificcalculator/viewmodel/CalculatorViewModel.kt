package com.university.scientificcalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.university.scientificcalculator.model.CalculationHistory
import com.university.scientificcalculator.model.CalculationType
import com.university.scientificcalculator.utils.CalculatorEngine
import kotlin.math.abs

/**
 * CalculatorViewModel
 *
 * Follows the MVVM (Model-View-ViewModel) architecture pattern.
 * Holds all UI state so it survives configuration changes (screen rotation).
 *
 * LiveData objects are observed by MainActivity; the ViewModel never
 * holds a reference to any View or Context.
 */
class CalculatorViewModel : ViewModel() {

    // ─── LiveData (observed by the View) ─────────────────────────────────────

    /** The current expression being built (shown in the secondary display) */
    private val _expression = MutableLiveData<String>("")
    val expression: LiveData<String> = _expression

    /** The current value shown on the main display */
    private val _display = MutableLiveData<String>("0")
    val display: LiveData<String> = _display

    /** Whether the calculator is in Scientific mode */
    private val _scientificMode = MutableLiveData<Boolean>(false)
    val scientificMode: LiveData<Boolean> = _scientificMode

    /** Whether we are in a post-result state (next input clears display) */
    private val _isResultState = MutableLiveData<Boolean>(false)
    val isResultState: LiveData<Boolean> = _isResultState

    /** Calculation history list */
    private val _history = MutableLiveData<List<CalculationHistory>>(emptyList())
    val history: LiveData<List<CalculationHistory>> = _history

    /** Whether trig functions operate in Degrees (true) or Radians (false) */
    private val _degreeMode = MutableLiveData<Boolean>(true)
    val degreeMode: LiveData<Boolean> = _degreeMode

    // ─── Internal State ───────────────────────────────────────────────────────

    private var currentInput: StringBuilder = StringBuilder("0")
    private var pendingOperator: String? = null
    private var previousValue: Double? = null
    private var hasDecimal: Boolean = false
    private var justEvaluated: Boolean = false
    private val historyList = mutableListOf<CalculationHistory>()

    // ─── Button Handlers ──────────────────────────────────────────────────────

    /**
     * Called when a digit (0–9) button is tapped.
     */
    fun onDigit(digit: String) {
        if (justEvaluated) {
            // Start fresh after a result was shown
            currentInput.clear()
            currentInput.append(digit)
            justEvaluated = false
            hasDecimal = false
        } else {
            if (currentInput.toString() == "0" && digit != ".") {
                currentInput.clear()
            }
            currentInput.append(digit)
        }
        updateDisplay()
    }

    /**
     * Called when the decimal point button is tapped.
     */
    fun onDecimal() {
        if (justEvaluated) {
            currentInput.clear()
            currentInput.append("0")
            justEvaluated = false
        }
        if (!hasDecimal) {
            if (currentInput.isEmpty()) currentInput.append("0")
            currentInput.append(".")
            hasDecimal = true
            updateDisplay()
        }
    }

    /**
     * Called when an operator (+, −, ×, ÷) is tapped.
     */
    fun onOperator(op: String) {
        val currentValue = currentInput.toString().toDoubleOrNull()

        if (justEvaluated && currentValue != null) {
            // Chain operator onto result
            previousValue = currentValue
            pendingOperator = op
            appendToExpression(CalculatorEngine.formatResult(currentValue) + " $op ")
            currentInput.clear()
            hasDecimal = false
            justEvaluated = false
            return
        }

        if (currentValue != null && pendingOperator != null && previousValue != null) {
            // Evaluate the pending operation first
            val result = applyOperator(previousValue!!, currentValue, pendingOperator!!)
            previousValue = result
            _expression.value = (_expression.value ?: "") + CalculatorEngine.formatResult(currentValue) + " $op "
            currentInput.clear()
            currentInput.append(CalculatorEngine.formatResult(result))
            hasDecimal = CalculatorEngine.formatResult(result).contains(".")
        } else if (currentValue != null) {
            previousValue = currentValue
            appendToExpression(CalculatorEngine.formatResult(currentValue) + " $op ")
            currentInput.clear()
            hasDecimal = false
        }

        pendingOperator = op
        updateDisplay()
    }

    /**
     * Called when = is tapped.
     * Evaluates the full expression and displays the result.
     */
    fun onEquals() {
        val currentValue = currentInput.toString().toDoubleOrNull() ?: return
        if (pendingOperator == null || previousValue == null) return

        val fullExpression = (_expression.value ?: "") + CalculatorEngine.formatResult(currentValue)
        val result = applyOperator(previousValue!!, currentValue, pendingOperator!!)
        val resultStr = CalculatorEngine.formatResult(result)

        currentInput.clear()
        currentInput.append(resultStr)
        hasDecimal = resultStr.contains(".")

        _expression.value = "$fullExpression ="
        _display.value = resultStr

        addToHistory(fullExpression, resultStr, CalculationType.BASIC)

        pendingOperator = null
        previousValue = null
        justEvaluated = true
    }

    /**
     * Applies a binary operator and returns the result.
     */
    private fun applyOperator(a: Double, b: Double, op: String): Double {
        return when (op) {
            "+"  -> a + b
            "−"  -> a - b
            "×"  -> a * b
            "÷"  -> {
                if (abs(b) < 1e-15) throw ArithmeticException("Division by zero")
                a / b
            }
            "^"  -> CalculatorEngine.power(a, b)
            "nPr" -> CalculatorEngine.permutation(a, b)
            "nCr" -> CalculatorEngine.combination(a, b)
            else -> throw IllegalArgumentException("Unknown operator: $op")
        }
    }

    // ─── Scientific Functions ─────────────────────────────────────────────────

    /**
     * Applies a single-argument scientific function to the current display value.
     * @param func Name of the function (e.g. "sin", "sqrt", "log")
     */
    fun onScientificFunction(func: String) {
        val input = currentInput.toString().toDoubleOrNull() ?: return
        val deg = _degreeMode.value ?: true

        val result = try {
            when (func) {
                "sin"   -> if (deg) CalculatorEngine.sin(input) else kotlin.math.sin(input)
                "cos"   -> if (deg) CalculatorEngine.cos(input) else kotlin.math.cos(input)
                "tan"   -> if (deg) CalculatorEngine.tan(input) else kotlin.math.tan(input)
                "sinh"  -> CalculatorEngine.sinh(input)
                "cosh"  -> CalculatorEngine.cosh(input)
                "tanh"  -> CalculatorEngine.tanh(input)
                "sqrt"  -> CalculatorEngine.sqrt(input)
                "log"   -> CalculatorEngine.log10(input)
                "ln"    -> CalculatorEngine.ln(input)
                "%"     -> CalculatorEngine.percentage(input)
                "n!"    -> CalculatorEngine.factorial(input)
                "x²"    -> CalculatorEngine.power(input, 2.0)
                "x³"    -> CalculatorEngine.power(input, 3.0)
                "1/x"   -> if (abs(input) < 1e-15) throw ArithmeticException("Division by zero") else 1.0 / input
                "eˣ"    -> Math.E.pow(input)
                "10ˣ"   -> Math.pow(10.0, input)
                "abs"   -> abs(input)
                else    -> return
            }
        } catch (e: ArithmeticException) {
            _display.value = "Error: ${e.message}"
            _expression.value = "$func($input) ="
            return
        }

        val expression = "$func($input)"
        val resultStr = CalculatorEngine.formatResult(result)

        _expression.value = "$expression ="
        currentInput.clear()
        currentInput.append(resultStr)
        hasDecimal = resultStr.contains(".")
        _display.value = resultStr
        justEvaluated = true

        addToHistory(expression, resultStr, CalculationType.SCIENTIFIC)
    }

    /** Sets pending operator to "^" (power) and waits for the exponent */
    fun onPowerOperator() = onOperator("^")

    /** Sets pending operator to "nPr" */
    fun onPermutation() = onOperator("nPr")

    /** Sets pending operator to "nCr" */
    fun onCombination() = onOperator("nCr")

    // ─── Control Buttons ──────────────────────────────────────────────────────

    /**
     * Clears the entire calculator state (All Clear).
     */
    fun onClear() {
        currentInput.clear()
        currentInput.append("0")
        pendingOperator = null
        previousValue = null
        hasDecimal = false
        justEvaluated = false
        _expression.value = ""
        _display.value = "0"
    }

    /**
     * Deletes the last character entered (Backspace).
     */
    fun onDelete() {
        if (justEvaluated) {
            onClear()
            return
        }
        if (currentInput.length > 1) {
            val removedChar = currentInput[currentInput.length - 1]
            if (removedChar == '.') hasDecimal = false
            currentInput.deleteCharAt(currentInput.length - 1)
        } else {
            currentInput.clear()
            currentInput.append("0")
        }
        updateDisplay()
    }

    /**
     * Toggles between positive and negative sign.
     */
    fun onToggleSign() {
        val value = currentInput.toString().toDoubleOrNull() ?: return
        val toggled = -value
        currentInput.clear()
        currentInput.append(CalculatorEngine.formatResult(toggled))
        hasDecimal = currentInput.contains(".")
        updateDisplay()
    }

    /**
     * Inserts a constant into the display.
     */
    fun onConstant(constant: String) {
        val value = when (constant) {
            "π"  -> Math.PI
            "e"  -> Math.E
            else -> return
        }
        val str = CalculatorEngine.formatResult(value)
        currentInput.clear()
        currentInput.append(str)
        hasDecimal = str.contains(".")
        justEvaluated = true
        updateDisplay()
    }

    // ─── Mode Toggles ─────────────────────────────────────────────────────────

    fun toggleScientificMode() {
        _scientificMode.value = !(_scientificMode.value ?: false)
    }

    fun toggleDegreeMode() {
        _degreeMode.value = !(_degreeMode.value ?: true)
    }

    // ─── History ──────────────────────────────────────────────────────────────

    fun clearHistory() {
        historyList.clear()
        _history.value = emptyList()
    }

    fun addExternalHistory(expression: String, result: String, type: CalculationType) {
        addToHistory(expression, result, type)
    }

    private fun addToHistory(expression: String, result: String, type: CalculationType) {
        val entry = CalculationHistory(expression = expression, result = result, type = type)
        historyList.add(0, entry)  // Add to front so newest is at top
        _history.value = historyList.toList()
    }

    // ─── Internal Helpers ─────────────────────────────────────────────────────

    private fun updateDisplay() {
        _display.value = currentInput.toString().ifEmpty { "0" }
    }

    private fun appendToExpression(text: String) {
        _expression.value = (_expression.value ?: "") + text
    }

    // Called by Android when ViewModel is about to be destroyed (screen exit)
    override fun onCleared() {
        super.onCleared()
        // Could persist history to database here in a production app
    }
}

private fun Double.pow(exponent: Double): Double = Math.pow(this, exponent)
private fun Double.pow(exponent: Int): Double = Math.pow(this, exponent.toDouble())

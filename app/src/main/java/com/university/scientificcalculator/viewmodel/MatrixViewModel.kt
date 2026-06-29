package com.university.scientificcalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.university.scientificcalculator.utils.MatrixEngine

/**
 * MatrixViewModel
 *
 * Stores the state for the MatrixActivity: matrix size, input values,
 * selected operation, and computation result.
 */
class MatrixViewModel : ViewModel() {

    /** Selected matrix size (2, 3, or 4) */
    private val _matrixSize = MutableLiveData<Int>(2)
    val matrixSize: LiveData<Int> = _matrixSize

    /** Current result string to display */
    private val _result = MutableLiveData<String>("")
    val result: LiveData<String> = _result

    /** Error message if operation fails */
    private val _error = MutableLiveData<String>("")
    val error: LiveData<String> = _error

    /** Selected operation (ADD, SUBTRACT, MULTIPLY, DETERMINANT) */
    private val _operation = MutableLiveData<String>("ADD")
    val operation: LiveData<String> = _operation

    fun setMatrixSize(size: Int) {
        _matrixSize.value = size
        _result.value = ""
        _error.value = ""
    }

    fun setOperation(op: String) {
        _operation.value = op
    }

    /**
     * Performs the selected operation on matrix A (and B if needed).
     *
     * @param valuesA  Flat list of strings from matrix A's EditText cells
     * @param valuesB  Flat list of strings from matrix B's EditText cells (may be empty for determinant)
     */
    fun calculate(valuesA: List<String>, valuesB: List<String>) {
        val size = _matrixSize.value ?: 2
        val op = _operation.value ?: "ADD"

        try {
            val matrixA = MatrixEngine.parseMatrix(valuesA, size)

            when (op) {
                "DETERMINANT" -> {
                    val det = MatrixEngine.determinant(matrixA)
                    _result.value = "det(A) = ${MatrixEngine.formatValue(det)}"
                    _error.value = ""
                }
                else -> {
                    val matrixB = MatrixEngine.parseMatrix(valuesB, size)
                    val resultMatrix = when (op) {
                        "ADD"      -> MatrixEngine.add(matrixA, matrixB)
                        "SUBTRACT" -> MatrixEngine.subtract(matrixA, matrixB)
                        "MULTIPLY" -> MatrixEngine.multiply(matrixA, matrixB)
                        else       -> throw IllegalArgumentException("Unknown operation: $op")
                    }
                    _result.value = MatrixEngine.formatMatrix(resultMatrix)
                    _error.value = ""
                }
            }
        } catch (e: NumberFormatException) {
            _error.value = "Please enter valid numbers in all cells."
            _result.value = ""
        } catch (e: Exception) {
            _error.value = "Error: ${e.message}"
            _result.value = ""
        }
    }

    fun clearResult() {
        _result.value = ""
        _error.value = ""
    }
}

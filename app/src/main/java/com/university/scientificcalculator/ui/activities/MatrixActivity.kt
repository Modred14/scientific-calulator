package com.university.scientificcalculator.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.university.scientificcalculator.R
import com.university.scientificcalculator.databinding.ActivityMatrixBinding
import com.university.scientificcalculator.viewmodel.MatrixViewModel

/**
 * MatrixActivity
 *
 * Provides a UI for 2×2, 3×3, and 4×4 matrix operations:
 * Addition, Subtraction, Multiplication, and Determinant.
 *
 * Matrix cells are generated programmatically based on the selected size.
 *
 * LIFECYCLE NOTE:
 * This Activity demonstrates onResume() for refreshing state when returning
 * from another screen, and onSaveInstanceState() to preserve matrix size
 * across rotation.
 */
class MatrixActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MatrixActivity"
        private const val KEY_MATRIX_SIZE = "matrix_size"
        private const val KEY_OPERATION = "operation"
    }

    private lateinit var binding: ActivityMatrixBinding
    private val viewModel: MatrixViewModel by viewModels()

    // Dynamically generated EditText cells for matrix A and B
    private val matrixACells = mutableListOf<EditText>()
    private val matrixBCells = mutableListOf<EditText>()

    private var currentSize = 2

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    /**
     * onCreate() — Sets up the UI, spinners, and button listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "▶ onCreate() called")

        binding = ActivityMatrixBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Matrix Calculator"

        // Restore size from saved state if available
        currentSize = savedInstanceState?.getInt(KEY_MATRIX_SIZE) ?: 2
        val savedOp = savedInstanceState?.getString(KEY_OPERATION) ?: "ADD"

        setupSizeSpinner()
        setupOperationButtons(savedOp)
        buildMatrixGrids(currentSize)
        observeViewModel()

        binding.btnCalculate.setOnClickListener { performCalculation() }
        binding.btnClear.setOnClickListener { clearAll() }

        Log.d(TAG, "✅ onCreate() complete — Matrix size: $currentSize")
    }

    /**
     * onResume() — Ensures the UI is correctly showing the selected operation
     * when returning from another Activity.
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "▶ onResume() called — MatrixActivity is interactive")
        updateOperationHighlight(viewModel.operation.value ?: "ADD")
    }

    /**
     * onPause() — Called when the user navigates away.
     */
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "▶ onPause() called — MatrixActivity is losing focus")
    }

    /**
     * onStop() — Called when the Activity is no longer visible.
     */
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "▶ onStop() called — MatrixActivity is hidden")
    }

    /**
     * onDestroy() — Final cleanup.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "▶ onDestroy() called — MatrixActivity is being destroyed")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_MATRIX_SIZE, currentSize)
        outState.putString(KEY_OPERATION, viewModel.operation.value)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    // ─── UI Setup ─────────────────────────────────────────────────────────────

    private fun setupSizeSpinner() {
        val sizes = arrayOf("2×2", "3×3", "4×4")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSize.adapter = adapter
        binding.spinnerSize.setSelection(currentSize - 2)

        binding.spinnerSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                currentSize = pos + 2
                viewModel.setMatrixSize(currentSize)
                buildMatrixGrids(currentSize)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupOperationButtons(defaultOp: String) {
        binding.btnAdd.setOnClickListener      { selectOperation("ADD") }
        binding.btnSubtract.setOnClickListener { selectOperation("SUBTRACT") }
        binding.btnMultiply.setOnClickListener { selectOperation("MULTIPLY") }
        binding.btnDeterminant.setOnClickListener { selectOperation("DETERMINANT") }
        selectOperation(defaultOp)
    }

    private fun selectOperation(op: String) {
        viewModel.setOperation(op)
        updateOperationHighlight(op)

        // Show Matrix B only when the operation requires two matrices
        val needsTwoMatrices = op != "DETERMINANT"
        binding.labelMatrixB.isVisible = needsTwoMatrices
        binding.containerMatrixB.isVisible = needsTwoMatrices
    }

    private fun updateOperationHighlight(op: String) {
        binding.btnAdd.isSelected      = op == "ADD"
        binding.btnSubtract.isSelected = op == "SUBTRACT"
        binding.btnMultiply.isSelected = op == "MULTIPLY"
        binding.btnDeterminant.isSelected = op == "DETERMINANT"
    }

    /**
     * Dynamically builds n×n grids of EditText cells for both matrices.
     * Clears existing cells before re-building.
     */
    private fun buildMatrixGrids(n: Int) {
        buildGrid(binding.containerMatrixA, matrixACells, n)
        buildGrid(binding.containerMatrixB, matrixBCells, n)
        viewModel.clearResult()
    }

    private fun buildGrid(container: LinearLayout, cells: MutableList<EditText>, n: Int) {
        container.removeAllViews()
        cells.clear()

        val cellSizeDp = when (n) {
            2 -> 80
            3 -> 70
            else -> 60
        }
        val cellSizePx = (cellSizeDp * resources.displayMetrics.density).toInt()
        val margin = (4 * resources.displayMetrics.density).toInt()

        for (row in 0 until n) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            for (col in 0 until n) {
                val editText = EditText(this).apply {
                    hint = "0"
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                                android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
                    background = getDrawable(R.drawable.bg_matrix_cell)
                    setTextColor(getColor(R.color.text_primary))
                    setHintTextColor(getColor(R.color.text_hint))
                    textSize = 16f
                    layoutParams = LinearLayout.LayoutParams(cellSizePx, cellSizePx).apply {
                        setMargins(margin, margin, margin, margin)
                    }
                    setPadding(8, 8, 8, 8)
                }
                cells.add(editText)
                rowLayout.addView(editText)
            }
            container.addView(rowLayout)
        }
    }

    // ─── Calculation ──────────────────────────────────────────────────────────

    private fun performCalculation() {
        val aValues = matrixACells.map { it.text.toString() }
        val bValues = matrixBCells.map { it.text.toString() }
        viewModel.calculate(aValues, bValues)
    }

    private fun clearAll() {
        matrixACells.forEach { it.setText("") }
        matrixBCells.forEach { it.setText("") }
        viewModel.clearResult()
    }

    // ─── Observers ────────────────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.result.observe(this) { result ->
            if (result.isNotEmpty()) {
                binding.tvResult.text = result
                binding.cardResult.isVisible = true
            } else {
                binding.cardResult.isVisible = false
            }
        }

        viewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                binding.tvError.text = error
                binding.tvError.isVisible = true
                binding.cardResult.isVisible = false
            } else {
                binding.tvError.isVisible = false
            }
        }
    }
}

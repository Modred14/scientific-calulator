package com.university.scientificcalculator.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.university.scientificcalculator.databinding.ActivityStatisticsBinding
import com.university.scientificcalculator.utils.CalculatorEngine
import com.university.scientificcalculator.viewmodel.StatisticsViewModel

/**
 * StatisticsActivity
 *
 * Provides a UI for entering a dataset and computing:
 * Mean, Median, Mode, Population Std Dev, Sample Std Dev,
 * Min, Max, Range, and Sum.
 *
 * LIFECYCLE NOTE:
 * Demonstrates onStart() for registering keyboard listeners and
 * onStop() for releasing resources.
 */
class StatisticsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "StatisticsActivity"
    }

    private lateinit var binding: ActivityStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    /**
     * onCreate() — Sets up the layout, toolbar, and button listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "▶ onCreate() called")

        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Statistics Calculator"

        setupButtons()
        observeViewModel()

        Log.d(TAG, "✅ onCreate() complete")
    }

    /**
     * onStart() — Register the IME "Done" action on the EditText so
     * pressing the keyboard's Done key triggers calculation.
     */
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "▶ onStart() called — registering keyboard listener")

        binding.etDataset.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                calculate()
                true
            } else false
        }
    }

    /**
     * onResume() — Called when the Activity is fully interactive.
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "▶ onResume() called — StatisticsActivity is interactive")
    }

    /**
     * onPause() — Save the input text to avoid re-typing.
     */
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "▶ onPause() called — saving input state")
        // Could save binding.etDataset.text to SharedPreferences here
    }

    /**
     * onStop() — Unregister the keyboard listener.
     */
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "▶ onStop() called — removing keyboard listener")
        binding.etDataset.setOnEditorActionListener(null)
    }

    /**
     * onDestroy() — Final cleanup.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "▶ onDestroy() called")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    // ─── UI Setup ─────────────────────────────────────────────────────────────

    private fun setupButtons() {
        binding.btnCalculate.setOnClickListener { calculate() }
        binding.btnClear.setOnClickListener {
            binding.etDataset.setText("")
            viewModel.clear()
        }

        // Quick fill sample datasets for demonstration
        binding.btnSampleData.setOnClickListener {
            binding.etDataset.setText("4, 7, 2, 9, 4, 1, 8, 4, 3, 6")
        }
    }

    private fun calculate() {
        val input = binding.etDataset.text.toString()
        viewModel.calculate(input)
    }

    // ─── Observers ────────────────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.datasetDisplay.observe(this) { display ->
            binding.tvDatasetInfo.text = display
            binding.tvDatasetInfo.isVisible = display.isNotEmpty()
        }

        viewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                binding.tvError.text = error
                binding.tvError.isVisible = true
                binding.cardResults.isVisible = false
            } else {
                binding.tvError.isVisible = false
            }
        }

        viewModel.summary.observe(this) { summary ->
            if (summary != null) {
                binding.cardResults.isVisible = true

                binding.tvMean.text        = CalculatorEngine.formatResult(summary.mean)
                binding.tvMedian.text      = CalculatorEngine.formatResult(summary.median)
                binding.tvMode.text        = summary.mode
                binding.tvStdDev.text      = CalculatorEngine.formatResult(summary.stdDev)
                binding.tvSampleStdDev.text= CalculatorEngine.formatResult(summary.sampleStdDev)
                binding.tvMin.text         = CalculatorEngine.formatResult(summary.min)
                binding.tvMax.text         = CalculatorEngine.formatResult(summary.max)
                binding.tvRange.text       = CalculatorEngine.formatResult(summary.range)
                binding.tvSum.text         = CalculatorEngine.formatResult(summary.sum)
                binding.tvCount.text       = summary.count.toString()
            } else {
                binding.cardResults.isVisible = false
            }
        }
    }
}

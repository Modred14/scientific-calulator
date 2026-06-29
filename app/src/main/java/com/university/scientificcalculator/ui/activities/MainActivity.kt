package com.university.scientificcalculator.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.university.scientificcalculator.R
import com.university.scientificcalculator.databinding.ActivityMainBinding
import com.university.scientificcalculator.viewmodel.CalculatorViewModel

/**
 * MainActivity
 *
 * The primary screen of the Scientific Calculator. Hosts the basic and
 * scientific calculator UI. Observes CalculatorViewModel via LiveData.
 *
 * ANDROID LIFECYCLE METHODS DEMONSTRATED BELOW:
 *
 * ┌──────────┐    ┌──────────┐    ┌──────────┐
 * │ onCreate │ →  │ onStart  │ →  │ onResume │  (Activity becomes interactive)
 * └──────────┘    └──────────┘    └──────────┘
 *                                      ↕  (user switches app / navigates away)
 * ┌───────────┐    ┌──────────┐    ┌──────────┐
 * │ onDestroy │ ←  │  onStop  │ ←  │ onPause  │  (Activity goes to background)
 * └───────────┘    └──────────┘    └──────────┘
 */
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"           // Logcat tag
        private const val KEY_EXPRESSION = "expression" // Bundle key for state save
    }

    // ViewBinding eliminates all findViewById() calls
    private lateinit var binding: ActivityMainBinding

    // ViewModel scoped to this Activity; survives configuration changes
    private val viewModel: CalculatorViewModel by viewModels()

    // ─── LIFECYCLE: onCreate ───────────────────────────────────────────────

    /**
     * onCreate() — Called when the Activity is FIRST CREATED.
     *
     * This is where you:
     * - Set the content view / inflate layouts
     * - Initialize ViewBinding
     * - Restore saved instance state
     * - Set up ViewModels
     * - Observe LiveData
     * - Attach click listeners
     *
     * Runs once per Activity instance (unless the system recreates it after
     * a configuration change like screen rotation).
     *
     * @param savedInstanceState Bundle containing previously saved state,
     *                           or null if this is a fresh start.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "▶ onCreate() called — Activity is being created")

        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the Toolbar as the ActionBar for back navigation support
        setSupportActionBar(binding.toolbar)

        // Observe LiveData from ViewModel → update UI automatically
        observeViewModel()

        // Wire up all button click listeners
        setupBasicButtons()
        setupScientificButtons()
        setupNavigationButtons()

        // Restore saved state if coming back from a configuration change
        savedInstanceState?.let {
            val savedExpression = it.getString(KEY_EXPRESSION)
            Log.d(TAG, "onCreate: Restored expression: $savedExpression")
        }

        Log.d(TAG, "✅ onCreate() complete")
    }

    // ─── LIFECYCLE: onStart ────────────────────────────────────────────────

    /**
     * onStart() — Called when the Activity becomes VISIBLE to the user.
     *
     * The Activity is visible but NOT yet interactive. You can use this to:
     * - Start animations
     * - Register broadcast receivers
     * - Begin connecting to services
     *
     * Pairs with onStop().
     */
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "▶ onStart() called — Activity is now visible")

        // Example: start a subtle animation on the display panel
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.displayPanel.startAnimation(fadeIn)
    }

    // ─── LIFECYCLE: onResume ──────────────────────────────────────────────

    /**
     * onResume() — Called when the Activity is in the FOREGROUND and INTERACTIVE.
     *
     * This is the state where the Activity is fully running. Use this to:
     * - Resume paused animations, sensors, camera
     * - Re-register listeners
     * - Update the UI with any data that may have changed while paused
     *
     * Pairs with onPause().
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "▶ onResume() called — Activity is now interactive (foreground)")

        // The calculator is ready for user input.
        // In a real app this might re-subscribe to real-time data updates.
    }

    // ─── LIFECYCLE: onPause ───────────────────────────────────────────────

    /**
     * onPause() — Called when the Activity is PARTIALLY OBSCURED or about to
     * lose focus (another Activity comes to the foreground).
     *
     * Use this to:
     * - Save lightweight data
     * - Pause animations or ongoing operations
     * - Release camera/sensor resources
     *
     * IMPORTANT: Keep this method fast; the next Activity won't launch until
     * onPause() returns.
     *
     * Pairs with onResume().
     */
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "▶ onPause() called — Activity is partially obscured / losing focus")

        // In a full app we'd auto-save the current expression to SharedPreferences here
    }

    // ─── LIFECYCLE: onStop ────────────────────────────────────────────────

    /**
     * onStop() — Called when the Activity is NO LONGER VISIBLE to the user.
     *
     * Use this to:
     * - Stop heavy background operations
     * - Save persistent data (e.g. to Room database)
     * - Unregister broadcast receivers
     *
     * Pairs with onStart().
     */
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "▶ onStop() called — Activity is no longer visible")

        // Persist calculation history to SharedPreferences
        saveHistoryToPrefs()
    }

    // ─── LIFECYCLE: onDestroy ────────────────────────────────────────────

    /**
     * onDestroy() — Called before the Activity is DESTROYED.
     *
     * This is the final lifecycle callback. Use this to:
     * - Clean up resources that persist beyond the Activity lifecycle
     * - Cancel background threads (if not using coroutines/lifecycle-aware components)
     *
     * Note: ViewModel.onCleared() is called AFTER onDestroy().
     * Pairs with onCreate().
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "▶ onDestroy() called — Activity is being destroyed")
        // ViewBinding reference is automatically cleaned up
        // ViewModel will call onCleared() after this
    }

    // ─── LIFECYCLE: onSaveInstanceState ──────────────────────────────────

    /**
     * Called before the Activity may be killed (configuration change or
     * system-initiated process death). Saves transient UI state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current expression so it can be restored in onCreate()
        outState.putString(KEY_EXPRESSION, binding.tvExpression.text.toString())
        Log.d(TAG, "onSaveInstanceState: Saving expression state")
    }

    // ─── Menu ─────────────────────────────────────────────────────────────

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            R.id.menu_clear_history -> {
                viewModel.clearHistory()
                Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ─── LiveData Observers ───────────────────────────────────────────────

    /**
     * Sets up LiveData observers. Every time a LiveData value changes,
     * the corresponding UI element is updated automatically.
     */
    private fun observeViewModel() {
        // Update the main display when the value changes
        viewModel.display.observe(this) { value ->
            binding.tvDisplay.text = value
            // Auto-adjust text size if the number is long
            val textSize = when {
                value.length > 14 -> 28f
                value.length > 10 -> 36f
                else              -> 48f
            }
            binding.tvDisplay.textSize = textSize
        }

        // Update the expression line above the display
        viewModel.expression.observe(this) { expr ->
            binding.tvExpression.text = expr
        }

        // Show/hide scientific buttons panel when mode toggles
        viewModel.scientificMode.observe(this) { isScientific ->
            binding.scientificPanel.isVisible = isScientific
            binding.btnToggleScientific.text = if (isScientific) "BASIC" else "SCI"
        }

        // Update Degree/Radian toggle label
        viewModel.degreeMode.observe(this) { isDeg ->
            binding.btnDegRad.text = if (isDeg) "DEG" else "RAD"
        }
    }

    // ─── Button Setup ─────────────────────────────────────────────────────

    private fun setupBasicButtons() {
        // Digit buttons
        val digitButtons = mapOf(
            binding.btn0 to "0", binding.btn1 to "1", binding.btn2 to "2",
            binding.btn3 to "3", binding.btn4 to "4", binding.btn5 to "5",
            binding.btn6 to "6", binding.btn7 to "7", binding.btn8 to "8",
            binding.btn9 to "9"
        )
        digitButtons.forEach { (button, digit) ->
            button.setOnClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                animateButton(it)
                viewModel.onDigit(digit)
            }
        }

        // Decimal
        binding.btnDecimal.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            animateButton(it)
            viewModel.onDecimal()
        }

        // Operators
        binding.btnAdd.setOnClickListener      { animateButton(it); viewModel.onOperator("+") }
        binding.btnSubtract.setOnClickListener { animateButton(it); viewModel.onOperator("−") }
        binding.btnMultiply.setOnClickListener { animateButton(it); viewModel.onOperator("×") }
        binding.btnDivide.setOnClickListener   { animateButton(it); viewModel.onOperator("÷") }

        // Equals
        binding.btnEquals.setOnClickListener {
            animateButton(it)
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            viewModel.onEquals()
        }

        // Clear (AC)
        binding.btnClear.setOnClickListener {
            animateButton(it)
            viewModel.onClear()
        }

        // Delete (⌫)
        binding.btnDelete.setOnClickListener {
            animateButton(it)
            viewModel.onDelete()
        }

        // Toggle sign (+/-)
        binding.btnToggleSign.setOnClickListener {
            animateButton(it)
            viewModel.onToggleSign()
        }

        // Percentage
        binding.btnPercent.setOnClickListener {
            animateButton(it)
            viewModel.onScientificFunction("%")
        }

        // Scientific mode toggle
        binding.btnToggleScientific.setOnClickListener {
            animateButton(it)
            viewModel.toggleScientificMode()
        }
    }

    private fun setupScientificButtons() {
        // Trig functions
        binding.btnSin.setOnClickListener   { animateButton(it); viewModel.onScientificFunction("sin") }
        binding.btnCos.setOnClickListener   { animateButton(it); viewModel.onScientificFunction("cos") }
        binding.btnTan.setOnClickListener   { animateButton(it); viewModel.onScientificFunction("tan") }

        // Hyperbolic functions
        binding.btnSinh.setOnClickListener  { animateButton(it); viewModel.onScientificFunction("sinh") }
        binding.btnCosh.setOnClickListener  { animateButton(it); viewModel.onScientificFunction("cosh") }
        binding.btnTanh.setOnClickListener  { animateButton(it); viewModel.onScientificFunction("tanh") }

        // Power & roots
        binding.btnSqrt.setOnClickListener  { animateButton(it); viewModel.onScientificFunction("sqrt") }
        binding.btnSquare.setOnClickListener{ animateButton(it); viewModel.onScientificFunction("x²") }
        binding.btnCube.setOnClickListener  { animateButton(it); viewModel.onScientificFunction("x³") }
        binding.btnPower.setOnClickListener { animateButton(it); viewModel.onPowerOperator() }

        // Logarithms
        binding.btnLog.setOnClickListener   { animateButton(it); viewModel.onScientificFunction("log") }
        binding.btnLn.setOnClickListener    { animateButton(it); viewModel.onScientificFunction("ln") }

        // Other
        binding.btnFactorial.setOnClickListener { animateButton(it); viewModel.onScientificFunction("n!") }
        binding.btnReciprocal.setOnClickListener{ animateButton(it); viewModel.onScientificFunction("1/x") }
        binding.btnExp.setOnClickListener   { animateButton(it); viewModel.onScientificFunction("eˣ") }
        binding.btnTenX.setOnClickListener  { animateButton(it); viewModel.onScientificFunction("10ˣ") }
        binding.btnAbs.setOnClickListener   { animateButton(it); viewModel.onScientificFunction("abs") }

        // Permutation / Combination
        binding.btnNpr.setOnClickListener   { animateButton(it); viewModel.onPermutation() }
        binding.btnNcr.setOnClickListener   { animateButton(it); viewModel.onCombination() }

        // Constants
        binding.btnPi.setOnClickListener    { animateButton(it); viewModel.onConstant("π") }
        binding.btnE.setOnClickListener     { animateButton(it); viewModel.onConstant("e") }

        // Degree/Radian toggle
        binding.btnDegRad.setOnClickListener{ animateButton(it); viewModel.toggleDegreeMode() }
    }

    private fun setupNavigationButtons() {
        // Navigate to Matrix Calculator
        binding.btnMatrix.setOnClickListener {
            startActivity(Intent(this, MatrixActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Navigate to Statistics Calculator
        binding.btnStatistics.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    // ─── UI Helpers ───────────────────────────────────────────────────────

    /**
     * Applies a quick scale-down/up animation when a button is pressed.
     */
    private fun animateButton(view: View) {
        view.animate()
            .scaleX(0.92f).scaleY(0.92f)
            .setDuration(60)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(60).start()
            }.start()
    }

    /**
     * Persists the calculation history count to SharedPreferences.
     * In a full app this would serialize the full list to JSON or Room DB.
     */
    private fun saveHistoryToPrefs() {
        val prefs = getSharedPreferences("calc_prefs", MODE_PRIVATE)
        prefs.edit()
            .putInt("history_count", viewModel.history.value?.size ?: 0)
            .apply()
        Log.d(TAG, "History count saved to SharedPreferences")
    }
}

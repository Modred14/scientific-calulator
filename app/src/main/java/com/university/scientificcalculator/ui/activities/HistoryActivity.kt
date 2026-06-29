package com.university.scientificcalculator.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.university.scientificcalculator.databinding.ActivityHistoryBinding
import com.university.scientificcalculator.ui.adapters.HistoryAdapter
import com.university.scientificcalculator.viewmodel.CalculatorViewModel

/**
 * HistoryActivity
 *
 * Displays the list of past calculations using a RecyclerView.
 * Shares the same CalculatorViewModel as MainActivity so it reads
 * the same history list.
 *
 * LIFECYCLE NOTE:
 * Demonstrates how Activity back-stack works: this Activity is started
 * from MainActivity and pressing Back restores the MainActivity from
 * the back stack without recreating it (onRestart → onStart → onResume).
 */
class HistoryActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HistoryActivity"
    }

    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: CalculatorViewModel by viewModels()
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "▶ onCreate() called")

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Calculation History"

        setupRecyclerView()
        observeViewModel()

        binding.btnClearAll.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "▶ onResume() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "▶ onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "▶ onDestroy() called")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter()
        binding.recyclerHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerHistory.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.history.observe(this) { historyList ->
            if (historyList.isEmpty()) {
                binding.recyclerHistory.isVisible = false
                binding.tvEmpty.isVisible = true
            } else {
                binding.recyclerHistory.isVisible = true
                binding.tvEmpty.isVisible = false
                adapter.submitList(historyList)
            }
        }
    }
}

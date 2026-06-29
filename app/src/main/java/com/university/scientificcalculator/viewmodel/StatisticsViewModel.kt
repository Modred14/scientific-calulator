package com.university.scientificcalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.university.scientificcalculator.utils.CalculatorEngine
import com.university.scientificcalculator.utils.StatisticsEngine
import com.university.scientificcalculator.utils.StatsSummary

/**
 * StatisticsViewModel
 *
 * Holds data for StatisticsActivity. Parses raw text input into a dataset,
 * then exposes the computed statistics summary via LiveData.
 */
class StatisticsViewModel : ViewModel() {

    private val _summary = MutableLiveData<StatsSummary?>()
    val summary: LiveData<StatsSummary?> = _summary

    private val _error = MutableLiveData<String>("")
    val error: LiveData<String> = _error

    private val _datasetDisplay = MutableLiveData<String>("")
    val datasetDisplay: LiveData<String> = _datasetDisplay

    /**
     * Parses and computes statistics for the provided raw dataset string.
     * Accepts comma-separated, space-separated, or newline-separated numbers.
     */
    fun calculate(rawInput: String) {
        try {
            val data = StatisticsEngine.parseDataset(rawInput)
            _datasetDisplay.value = "Dataset (n=${data.size}): ${data.joinToString(", ") { CalculatorEngine.formatResult(it) }}"
            _summary.value = StatisticsEngine.fullSummary(data)
            _error.value = ""
        } catch (e: Exception) {
            _error.value = e.message ?: "Unknown error"
            _summary.value = null
            _datasetDisplay.value = ""
        }
    }

    fun clear() {
        _summary.value = null
        _error.value = ""
        _datasetDisplay.value = ""
    }
}

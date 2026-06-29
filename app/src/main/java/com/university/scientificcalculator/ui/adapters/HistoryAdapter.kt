package com.university.scientificcalculator.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.university.scientificcalculator.databinding.ItemHistoryBinding
import com.university.scientificcalculator.model.CalculationHistory
import com.university.scientificcalculator.model.CalculationType

/**
 * HistoryAdapter
 *
 * RecyclerView adapter for displaying CalculationHistory items.
 * Uses ListAdapter + DiffUtil for efficient list updates (only redraws
 * items that actually changed).
 */
class HistoryAdapter : ListAdapter<CalculationHistory, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // ─── ViewHolder ───────────────────────────────────────────────────────────

    class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalculationHistory) {
            binding.tvExpression.text = item.expression
            binding.tvResult.text = "= ${item.result}"
            binding.tvTime.text = item.formattedTime()

            // Show a colored label for the calculation type
            val (label, colorRes) = when (item.type) {
                CalculationType.BASIC       -> "Basic" to android.R.color.holo_blue_light
                CalculationType.SCIENTIFIC  -> "Scientific" to android.R.color.holo_green_light
                CalculationType.MATRIX      -> "Matrix" to android.R.color.holo_orange_light
                CalculationType.STATISTICS  -> "Statistics" to android.R.color.holo_purple
            }
            binding.tvType.text = label
        }
    }

    // ─── DiffUtil Callback ────────────────────────────────────────────────────

    /**
     * DiffUtil.ItemCallback tells the RecyclerView how to detect item changes
     * without comparing the entire list. This avoids unnecessary redraws.
     */
    class HistoryDiffCallback : DiffUtil.ItemCallback<CalculationHistory>() {
        override fun areItemsTheSame(old: CalculationHistory, new: CalculationHistory): Boolean =
            old.id == new.id

        override fun areContentsTheSame(old: CalculationHistory, new: CalculationHistory): Boolean =
            old == new
    }
}

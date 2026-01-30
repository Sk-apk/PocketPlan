package com.pocketplan.ui.dashboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.models.BudgetProgress
import com.pocketplan.models.BudgetStatus
import java.text.NumberFormat
import java.util.*

class BudgetProgressAdapter(
    private val budgetProgressList: List<BudgetProgress>
) : RecyclerView.Adapter<BudgetProgressAdapter.BudgetProgressViewHolder>() {

    inner class BudgetProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvBudgetProgress: CardView = itemView.findViewById(R.id.cvBudgetProgress)
        val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvBudgetRange: TextView = itemView.findViewById(R.id.tvBudgetRange)
        val tvActualSpent: TextView = itemView.findViewById(R.id.tvActualSpent)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val tvProgressPercentage: TextView = itemView.findViewById(R.id.tvProgressPercentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetProgressViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_budget_progress, parent, false)
        return BudgetProgressViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetProgressViewHolder, position: Int) {
        val progress = budgetProgressList[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        val context = holder.itemView.context

        holder.tvCategoryName.text = progress.categoryName
        holder.tvCategoryIcon.text = getIconEmoji(progress.categoryIcon)

        val minFormatted = currencyFormat.format(progress.minBudget)
        val maxFormatted = currencyFormat.format(progress.maxBudget)
        holder.tvBudgetRange.text = "Budget: $minFormatted - $maxFormatted"

        holder.tvActualSpent.text = "Spent: ${currencyFormat.format(progress.actualSpent)}"

        // Set progress bar
        val progressValue = progress.progressPercentage.toInt().coerceIn(0, 200)
        holder.progressBar.progress = progressValue
        holder.tvProgressPercentage.text = String.format("%.0f%%", progress.progressPercentage)

        // Set status and colors
        when (progress.status) {
            BudgetStatus.UNDER_MIN -> {
                holder.tvStatus.text = "⬇️ Below Target"
                holder.tvStatus.setTextColor(Color.parseColor("#2196F3")) // Blue
                holder.progressBar.progressTintList = context.getColorStateList(R.color.primary)
                try {
                    holder.cvBudgetProgress.setCardBackgroundColor(Color.parseColor(progress.categoryColor))
                } catch (e: Exception) {
                    holder.cvBudgetProgress.setCardBackgroundColor(Color.parseColor("#8B7BA8"))
                }
            }
            BudgetStatus.ON_TRACK -> {
                holder.tvStatus.text = "✅ On Track"
                holder.tvStatus.setTextColor(context.getColor(R.color.success))
                holder.progressBar.progressTintList = context.getColorStateList(R.color.success)
                try {
                    holder.cvBudgetProgress.setCardBackgroundColor(Color.parseColor(progress.categoryColor))
                } catch (e: Exception) {
                    holder.cvBudgetProgress.setCardBackgroundColor(Color.parseColor("#8B7BA8"))
                }
            }
            BudgetStatus.OVER_MAX -> {
                holder.tvStatus.text = "⚠️ Overspending!"
                holder.tvStatus.setTextColor(context.getColor(R.color.error))
                holder.progressBar.progressTintList = context.getColorStateList(R.color.error)
                holder.cvBudgetProgress.setCardBackgroundColor(context.getColor(R.color.error))
            }
        }
    }

    override fun getItemCount(): Int = budgetProgressList.size

    private fun getIconEmoji(icon: String): String {
        return when (icon) {
            "groceries" -> "🛒"
            "entertainment" -> "🎬"
            "transport" -> "🚗"
            "health" -> "💊"
            "education" -> "📚"
            "shopping" -> "🛍️"
            "bills" -> "💡"
            "savings" -> "💰"
            "food" -> "🍔"
            "travel" -> "✈️"
            "budget" -> "💵"
            else -> "📁"
        }
    }
}
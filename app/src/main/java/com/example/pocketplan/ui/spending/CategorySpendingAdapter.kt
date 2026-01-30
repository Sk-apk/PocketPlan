package com.pocketplan.ui.spending

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.models.CategorySpending
import java.text.NumberFormat
import java.util.*

class CategorySpendingAdapter(
    private val categorySpendingList: List<CategorySpending>
) : RecyclerView.Adapter<CategorySpendingAdapter.CategorySpendingViewHolder>() {

    inner class CategorySpendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCategorySpending: CardView = itemView.findViewById(R.id.cvCategorySpending)
        val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvTotalSpent: TextView = itemView.findViewById(R.id.tvTotalSpent)
        val tvExpenseCount: TextView = itemView.findViewById(R.id.tvExpenseCount)
        val tvPercentage: TextView = itemView.findViewById(R.id.tvPercentage)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySpendingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_spending, parent, false)
        return CategorySpendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategorySpendingViewHolder, position: Int) {
        val categorySpending = categorySpendingList[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

        holder.tvCategoryName.text = categorySpending.categoryName
        holder.tvCategoryIcon.text = getIconEmoji(categorySpending.categoryIcon)
        holder.tvTotalSpent.text = currencyFormat.format(categorySpending.totalSpent)
        holder.tvExpenseCount.text = "${categorySpending.expenseCount} expense${if (categorySpending.expenseCount != 1) "s" else ""}"
        holder.tvPercentage.text = String.format("%.1f%%", categorySpending.percentage)

        // Set progress bar
        holder.progressBar.progress = categorySpending.percentage.toInt()

        // Set card color
        try {
            holder.cvCategorySpending.setCardBackgroundColor(Color.parseColor(categorySpending.categoryColor))
        } catch (e: Exception) {
            holder.cvCategorySpending.setCardBackgroundColor(Color.parseColor("#8B7BA8"))
        }
    }

    override fun getItemCount(): Int = categorySpendingList.size

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
            else -> "📁"
        }
    }
}
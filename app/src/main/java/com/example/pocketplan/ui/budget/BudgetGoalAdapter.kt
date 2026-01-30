package com.pocketplan.ui.budget

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.models.BudgetGoal
import java.text.NumberFormat
import java.util.*

class BudgetGoalAdapter(
    private val budgetGoals: List<BudgetGoal>,
    private val onDeleteClick: (BudgetGoal) -> Unit
) : RecyclerView.Adapter<BudgetGoalAdapter.BudgetGoalViewHolder>() {

    inner class BudgetGoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvBudgetGoal: CardView = itemView.findViewById(R.id.cvBudgetGoal)
        val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvBudgetRange: TextView = itemView.findViewById(R.id.tvBudgetRange)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetGoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_budget_goal, parent, false)
        return BudgetGoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetGoalViewHolder, position: Int) {
        val budgetGoal = budgetGoals[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

        holder.tvCategoryName.text = budgetGoal.categoryName
        holder.tvCategoryIcon.text = getIconEmoji(budgetGoal.categoryIcon)

        val minFormatted = currencyFormat.format(budgetGoal.minAmount)
        val maxFormatted = currencyFormat.format(budgetGoal.maxAmount)
        holder.tvBudgetRange.text = "Min: $minFormatted\nMax: $maxFormatted"

        try {
            holder.cvBudgetGoal.setCardBackgroundColor(Color.parseColor(budgetGoal.categoryColor))
        } catch (e: Exception) {
            holder.cvBudgetGoal.setCardBackgroundColor(Color.parseColor("#8B7BA8"))
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(budgetGoal)
        }
    }

    override fun getItemCount(): Int = budgetGoals.size

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
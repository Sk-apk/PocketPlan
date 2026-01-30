package com.pocketplan.ui.expenses

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.models.Expense
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    private val expenses: List<Expense>,
    private val onItemClick: (Expense) -> Unit,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvExpense: CardView = itemView.findViewById(R.id.cvExpense)
        val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val ivReceiptIndicator: ImageView = itemView.findViewById(R.id.ivReceiptIndicator)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

        holder.tvDescription.text = expense.description
        holder.tvCategoryName.text = expense.categoryName
        holder.tvAmount.text = currencyFormat.format(expense.amount)
        holder.tvCategoryIcon.text = getIconEmoji(expense.categoryIcon)

        // Format date
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(expense.date)
            holder.tvDate.text = date?.let { outputFormat.format(it) } ?: expense.date
        } catch (e: Exception) {
            holder.tvDate.text = expense.date
        }

        // Show receipt indicator if photo exists
        if (expense.photoPath != null) {
            holder.ivReceiptIndicator.visibility = View.VISIBLE
        } else {
            holder.ivReceiptIndicator.visibility = View.GONE
        }

        // Set card color based on category
        try {
            holder.cvExpense.setCardBackgroundColor(Color.parseColor(expense.categoryColor))
        } catch (e: Exception) {
            holder.cvExpense.setCardBackgroundColor(Color.parseColor("#8B7BA8"))
        }

        // Click to view receipt
        holder.cvExpense.setOnClickListener {
            onItemClick(expense)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(expense)
        }
    }

    override fun getItemCount(): Int = expenses.size

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
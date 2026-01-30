package com.pocketplan.ui.categories

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.models.Category

class CategoryAdapter(
    private val categories: List<Category>,
    private val onDeleteClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCategory: CardView = itemView.findViewById(R.id.cvCategory)
        val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        holder.tvCategoryName.text = category.categoryName
        holder.tvCategoryIcon.text = getIconEmoji(category.categoryIcon)

        try {
            holder.cvCategory.setCardBackgroundColor(Color.parseColor(category.categoryColor))
        } catch (e: Exception) {
            holder.cvCategory.setCardBackgroundColor(Color.parseColor("#8B7BA8"))
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size

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
package com.pocketplan.ui.shared

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.models.SharedBudget

class SharedBudgetAdapter(
    private val sharedBudgets: List<SharedBudget>,
    private val onItemClick: (SharedBudget) -> Unit,
    private val onDeleteClick: (SharedBudget) -> Unit
) : RecyclerView.Adapter<SharedBudgetAdapter.SharedBudgetViewHolder>() {

    inner class SharedBudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvSharedBudget: CardView = itemView.findViewById(R.id.cvSharedBudget)
        val tvBudgetIcon: TextView = itemView.findViewById(R.id.tvBudgetIcon)
        val tvBudgetName: TextView = itemView.findViewById(R.id.tvBudgetName)
        val tvOwnerName: TextView = itemView.findViewById(R.id.tvOwnerName)
        val tvMemberCount: TextView = itemView.findViewById(R.id.tvMemberCount)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedBudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shared_budget, parent, false)
        return SharedBudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: SharedBudgetViewHolder, position: Int) {
        val sharedBudget = sharedBudgets[position]

        holder.tvBudgetIcon.text = "👥"
        holder.tvBudgetName.text = sharedBudget.budgetName
        holder.tvOwnerName.text = "Owner: ${sharedBudget.ownerUsername}"
        holder.tvMemberCount.text = "${sharedBudget.memberCount} member${if (sharedBudget.memberCount != 1) "s" else ""}"

        holder.cvSharedBudget.setCardBackgroundColor(Color.parseColor("#8B7BA8"))

        holder.cvSharedBudget.setOnClickListener {
            onItemClick(sharedBudget)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(sharedBudget)
        }
    }

    override fun getItemCount(): Int = sharedBudgets.size
}
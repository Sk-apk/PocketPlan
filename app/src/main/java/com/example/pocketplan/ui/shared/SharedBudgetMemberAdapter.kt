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
import com.pocketplan.models.SharedBudgetMember
import java.text.SimpleDateFormat
import java.util.*

class SharedBudgetMemberAdapter(
    private val members: List<SharedBudgetMember>,
    private val onRemoveClick: (SharedBudgetMember) -> Unit
) : RecyclerView.Adapter<SharedBudgetMemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvMember: CardView = itemView.findViewById(R.id.cvMember)
        val tvMemberIcon: TextView = itemView.findViewById(R.id.tvMemberIcon)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvRole: TextView = itemView.findViewById(R.id.tvRole)
        val tvJoinedDate: TextView = itemView.findViewById(R.id.tvJoinedDate)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shared_budget_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]

        holder.tvMemberIcon.text = if (member.isOwner) "👑" else "👤"
        holder.tvUsername.text = member.username
        holder.tvRole.text = if (member.isOwner) "Owner" else "Member"

        // Format joined date
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(member.joinedAt)
            holder.tvJoinedDate.text = "Joined: ${date?.let { outputFormat.format(it) } ?: member.joinedAt}"
        } catch (e: Exception) {
            holder.tvJoinedDate.text = "Joined: ${member.joinedAt}"
        }

        // Color code by role
        if (member.isOwner) {
            holder.cvMember.setCardBackgroundColor(Color.parseColor("#FFA726")) // Orange for owner
        } else {
            holder.cvMember.setCardBackgroundColor(Color.parseColor("#8B7BA8")) // Purple for members
        }

        // Hide remove button for owner
        if (member.isOwner) {
            holder.btnRemove.visibility = View.GONE
        } else {
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnRemove.setOnClickListener {
                onRemoveClick(member)
            }
        }
    }

    override fun getItemCount(): Int = members.size
}
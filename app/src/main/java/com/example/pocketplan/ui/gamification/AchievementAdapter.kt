package com.pocketplan.ui.gamification

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.models.Achievement
import java.text.SimpleDateFormat
import java.util.*

class AchievementAdapter(
    private val achievements: List<Achievement>
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    inner class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvAchievement: CardView = itemView.findViewById(R.id.cvAchievement)
        val tvEmoji: TextView = itemView.findViewById(R.id.tvEmoji)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvRequirement: TextView = itemView.findViewById(R.id.tvRequirement)
        val tvUnlockedDate: TextView = itemView.findViewById(R.id.tvUnlockedDate)
        val tvLocked: TextView = itemView.findViewById(R.id.tvLocked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_achievements, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        val context = holder.itemView.context

        holder.tvTitle.text = achievement.title
        holder.tvDescription.text = achievement.description
        holder.tvRequirement.text = achievement.requirement

        if (achievement.isUnlocked) {
            // Unlocked achievement
            holder.tvEmoji.text = achievement.emoji
            holder.tvEmoji.alpha = 1.0f
            holder.tvLocked.visibility = View.GONE
            holder.cvAchievement.setCardBackgroundColor(context.getColor(R.color.primary))

            // Show unlocked date
            achievement.unlockedDate?.let { date ->
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val parsedDate = inputFormat.parse(date)
                    holder.tvUnlockedDate.text = "Unlocked: ${parsedDate?.let { outputFormat.format(it) }}"
                    holder.tvUnlockedDate.visibility = View.VISIBLE
                } catch (e: Exception) {
                    holder.tvUnlockedDate.visibility = View.GONE
                }
            }
        } else {
            // Locked achievement
            holder.tvEmoji.text = "🔒"
            holder.tvEmoji.alpha = 0.5f
            holder.tvLocked.visibility = View.VISIBLE
            holder.tvUnlockedDate.visibility = View.GONE
            holder.cvAchievement.setCardBackgroundColor(Color.parseColor("#BDBDBD"))
        }
    }

    override fun getItemCount(): Int = achievements.size
}
package com.pocketplan.models

data class Achievement(
    val type: AchievementType,
    val title: String,
    val description: String,
    val emoji: String,
    val requirement: String,
    var isUnlocked: Boolean = false,
    var unlockedDate: String? = null
)

enum class AchievementType {
    FIRST_EXPENSE,          // Add first expense
    EXPENSE_STREAK_7,       // Log expenses 7 days in a row
    EXPENSE_STREAK_30,      // Log expenses 30 days in a row
    BUDGET_SET,             // Set first budget goal
    BUDGET_MET,             // Meet budget goal for a month
    BUDGET_MET_3,           // Meet budget goals for 3 months
    UNDER_BUDGET,           // Finish month under budget
    CATEGORY_MASTER,        // Create 5 categories
    EXPENSE_100,            // Log 100 expenses
    SAVER,                  // Save 20% or more in a month
    RECEIPTS_10,            // Attach 10 receipt photos
}
package com.pocketplan.models

data class BudgetProgress(
    val categoryId: Int?,
    val categoryName: String,
    val categoryColor: String,
    val categoryIcon: String,
    val minBudget: Double,
    val maxBudget: Double,
    val actualSpent: Double,
    var progressPercentage: Double = 0.0,
    var status: BudgetStatus = BudgetStatus.ON_TRACK
)

enum class BudgetStatus {
    UNDER_MIN,      // Spending below minimum (good for savings goals)
    ON_TRACK,       // Spending within min-max range
    OVER_MAX        // Spending over maximum (overspending)
}
package com.pocketplan.models

data class BudgetGoal(
    val budgetId: Int = 0,
    val categoryId: Int? = null, // null means overall budget
    val minAmount: Double,
    val maxAmount: Double,
    val month: Int,
    val year: Int,
    val userId: Int,
    val createdAt: String = "",
    // Additional fields for display
    var categoryName: String = "Overall Budget",
    var categoryColor: String = "#8B7BA8",
    var categoryIcon: String = "budget"
)
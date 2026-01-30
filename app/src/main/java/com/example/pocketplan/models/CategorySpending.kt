package com.pocketplan.models

data class CategorySpending(
    val categoryId: Int,
    val categoryName: String,
    val categoryColor: String,
    val categoryIcon: String,
    val totalSpent: Double,
    val expenseCount: Int,
    var percentage: Double = 0.0
)
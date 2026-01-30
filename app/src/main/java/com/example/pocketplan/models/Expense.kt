package com.pocketplan.models

data class Expense(
    val expenseId: Int = 0,
    val amount: Double,
    val date: String,
    val description: String,
    val categoryId: Int,
    val photoPath: String? = null,
    val userId: Int,
    val createdAt: String = "",
    // Additional fields for display purposes
     var categoryName: String = "",
    var categoryColor: String = "",
    var categoryIcon: String = ""
)
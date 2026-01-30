package com.pocketplan.models

data class DailySpending(
    val date: String,
    val totalAmount: Double,
    val expenseCount: Int
)
package com.pocketplan.models

data class SharedBudget(
    val sharedBudgetId: Int = 0,
    val budgetName: String,
    val ownerId: Int,
    val createdAt: String = "",
    // For display purposes
    var ownerUsername: String = "",
    var memberCount: Int = 0
)
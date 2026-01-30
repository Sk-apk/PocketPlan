package com.pocketplan.models

data class SharedBudgetMember(
    val memberId: Int = 0,
    val sharedBudgetId: Int,
    val userId: Int,
    val joinedAt: String = "",
    // For display purposes
    var username: String = "",
    var isOwner: Boolean = false
)
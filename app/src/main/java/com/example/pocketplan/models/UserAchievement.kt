package com.pocketplan.models

data class UserAchievement(
    val achievementId: Int = 0,
    val achievementType: String,
    val userId: Int,
    val achievementDate: String = ""
)
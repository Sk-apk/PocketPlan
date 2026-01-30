package com.pocketplan.models

data class User(
    val userId: Int = 0,
    val username: String,
    val password: String,
    val createdAt: String = ""
)
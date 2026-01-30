package com.pocketplan.utils

object ValidationUtils {

    fun isValidUsername(username: String): Pair<Boolean, String> {
        return when {
            username.isEmpty() -> Pair(false, "Username cannot be empty")
            username.length < 3 -> Pair(false, "Username must be at least 3 characters")
            username.length > 20 -> Pair(false, "Username must be less than 20 characters")
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> Pair(false, "Username can only contain letters, numbers, and underscores")
            else -> Pair(true, "")
        }
    }

    fun isValidPassword(password: String): Pair<Boolean, String> {
        return when {
            password.isEmpty() -> Pair(false, "Password cannot be empty")
            password.length < 6 -> Pair(false, "Password must be at least 6 characters")
            password.length > 30 -> Pair(false, "Password must be less than 30 characters")
            else -> Pair(true, "")
        }
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): Pair<Boolean, String> {
        return if (password == confirmPassword) {
            Pair(true, "")
        } else {
            Pair(false, "Passwords do not match")
        }
    }
}
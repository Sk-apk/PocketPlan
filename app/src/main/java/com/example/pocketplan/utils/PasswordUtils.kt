package com.pocketplan.utils

import java.security.MessageDigest

object PasswordUtils {

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun verifyPassword(inputPassword: String, storedHash: String): Boolean {
        val inputHash = hashPassword(inputPassword)
        return inputHash == storedHash
    }
}
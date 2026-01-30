package com.pocketplan.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.pocketplan.models.User
import com.pocketplan.utils.PasswordUtils

class UserDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun registerUser(username: String, password: String): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val hashedPassword = PasswordUtils.hashPassword(password)
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_USERNAME, username)
                put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword)
            }

            val result = db.insert(DatabaseHelper.TABLE_USERS, null, values)
            result != -1L
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    fun loginUser(username: String, password: String): User? {
        val db = dbHelper.readableDatabase
        var user: User? = null

        try {
            val cursor: Cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                "${DatabaseHelper.COLUMN_USERNAME} = ?",
                arrayOf(username),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD))

                if (PasswordUtils.verifyPassword(password, storedPassword)) {
                    user = User(
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
                        username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)),
                        password = storedPassword,
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                    )
                }
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return user
    }

    fun isUsernameExists(username: String): Boolean {
        val db = dbHelper.readableDatabase
        var exists = false

        try {
            val cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                arrayOf(DatabaseHelper.COLUMN_USER_ID),
                "${DatabaseHelper.COLUMN_USERNAME} = ?",
                arrayOf(username),
                null, null, null
            )

            exists = cursor.count > 0
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return exists
    }
}
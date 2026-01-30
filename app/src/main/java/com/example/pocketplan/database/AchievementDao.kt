package com.pocketplan.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.pocketplan.models.UserAchievement

class AchievementDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun unlockAchievement(userId: Int, achievementType: String): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_ACHIEVEMENT_TYPE, achievementType)
                put(DatabaseHelper.COLUMN_ACHIEVEMENT_USER_ID, userId)
            }

            val result = db.insertWithOnConflict(
                DatabaseHelper.TABLE_USER_ACHIEVEMENTS,
                null,
                values,
                android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
            )
            result != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun getUserAchievements(userId: Int): List<UserAchievement> {
        val achievements = mutableListOf<UserAchievement>()
        val db = dbHelper.readableDatabase

        try {
            val cursor: Cursor = db.query(
                DatabaseHelper.TABLE_USER_ACHIEVEMENTS,
                null,
                "${DatabaseHelper.COLUMN_ACHIEVEMENT_USER_ID} = ?",
                arrayOf(userId.toString()),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                do {
                    val achievement = UserAchievement(
                        achievementId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACHIEVEMENT_ID)),
                        achievementType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACHIEVEMENT_TYPE)),
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACHIEVEMENT_USER_ID)),
                        achievementDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACHIEVEMENT_DATE))
                    )
                    achievements.add(achievement)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return achievements
    }

    fun isAchievementUnlocked(userId: Int, achievementType: String): Boolean {
        val db = dbHelper.readableDatabase
        var unlocked = false

        try {
            val cursor = db.query(
                DatabaseHelper.TABLE_USER_ACHIEVEMENTS,
                arrayOf(DatabaseHelper.COLUMN_ACHIEVEMENT_ID),
                "${DatabaseHelper.COLUMN_ACHIEVEMENT_TYPE} = ? AND ${DatabaseHelper.COLUMN_ACHIEVEMENT_USER_ID} = ?",
                arrayOf(achievementType, userId.toString()),
                null, null, null
            )

            unlocked = cursor.count > 0
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return unlocked
    }
}
package com.pocketplan.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.pocketplan.models.SharedBudget
import com.pocketplan.models.SharedBudgetMember

class SharedBudgetDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Create a new shared budget
    fun createSharedBudget(budgetName: String, ownerId: Int): Long {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_SHARED_BUDGET_NAME, budgetName)
                put(DatabaseHelper.COLUMN_SHARED_BUDGET_OWNER_ID, ownerId)
            }

            val sharedBudgetId = db.insert(DatabaseHelper.TABLE_SHARED_BUDGETS, null, values)

            // Automatically add owner as a member
            if (sharedBudgetId != -1L) {
                addMember(sharedBudgetId.toInt(), ownerId)
            }

            sharedBudgetId
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        } finally {
            db.close()
        }
    }

    // Add a member to shared budget
    fun addMember(sharedBudgetId: Int, userId: Int): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_MEMBER_SHARED_BUDGET_ID, sharedBudgetId)
                put(DatabaseHelper.COLUMN_MEMBER_USER_ID, userId)
            }

            val result = db.insertWithOnConflict(
                DatabaseHelper.TABLE_SHARED_BUDGET_MEMBERS,
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

    // Get all shared budgets for a user
    fun getUserSharedBudgets(userId: Int): List<SharedBudget> {
        val sharedBudgets = mutableListOf<SharedBudget>()
        val db = dbHelper.readableDatabase

        try {
            val query = """
                SELECT sb.*, u.${DatabaseHelper.COLUMN_USERNAME} as owner_username,
                       (SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_SHARED_BUDGET_MEMBERS} 
                        WHERE ${DatabaseHelper.COLUMN_MEMBER_SHARED_BUDGET_ID} = sb.${DatabaseHelper.COLUMN_SHARED_BUDGET_ID}) as member_count
                FROM ${DatabaseHelper.TABLE_SHARED_BUDGETS} sb
                INNER JOIN ${DatabaseHelper.TABLE_USERS} u 
                ON sb.${DatabaseHelper.COLUMN_SHARED_BUDGET_OWNER_ID} = u.${DatabaseHelper.COLUMN_USER_ID}
                INNER JOIN ${DatabaseHelper.TABLE_SHARED_BUDGET_MEMBERS} sbm 
                ON sb.${DatabaseHelper.COLUMN_SHARED_BUDGET_ID} = sbm.${DatabaseHelper.COLUMN_MEMBER_SHARED_BUDGET_ID}
                WHERE sbm.${DatabaseHelper.COLUMN_MEMBER_USER_ID} = ?
                ORDER BY sb.${DatabaseHelper.COLUMN_CREATED_AT} DESC
            """.trimIndent()

            val cursor: Cursor = db.rawQuery(query, arrayOf(userId.toString()))

            if (cursor.moveToFirst()) {
                do {
                    val sharedBudget = SharedBudget(
                        sharedBudgetId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SHARED_BUDGET_ID)),
                        budgetName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SHARED_BUDGET_NAME)),
                        ownerId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SHARED_BUDGET_OWNER_ID)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                    )

                    sharedBudget.ownerUsername = cursor.getString(cursor.getColumnIndexOrThrow("owner_username"))
                    sharedBudget.memberCount = cursor.getInt(cursor.getColumnIndexOrThrow("member_count"))

                    sharedBudgets.add(sharedBudget)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return sharedBudgets
    }

    // Get all members of a shared budget
    fun getSharedBudgetMembers(sharedBudgetId: Int): List<SharedBudgetMember> {
        val members = mutableListOf<SharedBudgetMember>()
        val db = dbHelper.readableDatabase

        try {
            val query = """
                SELECT sbm.*, u.${DatabaseHelper.COLUMN_USERNAME}, 
                       sb.${DatabaseHelper.COLUMN_SHARED_BUDGET_OWNER_ID}
                FROM ${DatabaseHelper.TABLE_SHARED_BUDGET_MEMBERS} sbm
                INNER JOIN ${DatabaseHelper.TABLE_USERS} u 
                ON sbm.${DatabaseHelper.COLUMN_MEMBER_USER_ID} = u.${DatabaseHelper.COLUMN_USER_ID}
                INNER JOIN ${DatabaseHelper.TABLE_SHARED_BUDGETS} sb 
                ON sbm.${DatabaseHelper.COLUMN_MEMBER_SHARED_BUDGET_ID} = sb.${DatabaseHelper.COLUMN_SHARED_BUDGET_ID}
                WHERE sbm.${DatabaseHelper.COLUMN_MEMBER_SHARED_BUDGET_ID} = ?
                ORDER BY sbm.${DatabaseHelper.COLUMN_MEMBER_JOINED_AT} ASC
            """.trimIndent()

            val cursor: Cursor = db.rawQuery(query, arrayOf(sharedBudgetId.toString()))

            if (cursor.moveToFirst()) {
                do {
                    val member = SharedBudgetMember(
                        memberId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMBER_ID)),
                        sharedBudgetId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMBER_SHARED_BUDGET_ID)),
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMBER_USER_ID)),
                        joinedAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMBER_JOINED_AT))
                    )

                    member.username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME))
                    val ownerId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SHARED_BUDGET_OWNER_ID))
                    member.isOwner = member.userId == ownerId

                    members.add(member)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return members
    }

    // Remove a member from shared budget
    fun removeMember(memberId: Int): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val result = db.delete(
                DatabaseHelper.TABLE_SHARED_BUDGET_MEMBERS,
                "${DatabaseHelper.COLUMN_MEMBER_ID} = ?",
                arrayOf(memberId.toString())
            )
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    // Delete shared budget
    fun deleteSharedBudget(sharedBudgetId: Int): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val result = db.delete(
                DatabaseHelper.TABLE_SHARED_BUDGETS,
                "${DatabaseHelper.COLUMN_SHARED_BUDGET_ID} = ?",
                arrayOf(sharedBudgetId.toString())
            )
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    // Check if user is owner of shared budget
    fun isOwner(sharedBudgetId: Int, userId: Int): Boolean {
        val db = dbHelper.readableDatabase
        var isOwner = false

        try {
            val cursor = db.query(
                DatabaseHelper.TABLE_SHARED_BUDGETS,
                arrayOf(DatabaseHelper.COLUMN_SHARED_BUDGET_OWNER_ID),
                "${DatabaseHelper.COLUMN_SHARED_BUDGET_ID} = ?",
                arrayOf(sharedBudgetId.toString()),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                val ownerId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SHARED_BUDGET_OWNER_ID))
                isOwner = ownerId == userId
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return isOwner
    }

    // Get user ID by username (for inviting)
    fun getUserIdByUsername(username: String): Int? {
        val db = dbHelper.readableDatabase
        var userId: Int? = null

        try {
            val cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                arrayOf(DatabaseHelper.COLUMN_USER_ID),
                "${DatabaseHelper.COLUMN_USERNAME} = ?",
                arrayOf(username),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID))
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return userId
    }
}
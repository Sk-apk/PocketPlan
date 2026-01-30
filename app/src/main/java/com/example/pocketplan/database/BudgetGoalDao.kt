package com.pocketplan.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.pocketplan.models.BudgetGoal

class BudgetGoalDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun setBudgetGoal(budgetGoal: BudgetGoal): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                if (budgetGoal.categoryId != null) {
                    put(DatabaseHelper.COLUMN_BUDGET_CATEGORY_ID, budgetGoal.categoryId)
                }
                put(DatabaseHelper.COLUMN_BUDGET_MIN_AMOUNT, budgetGoal.minAmount)
                put(DatabaseHelper.COLUMN_BUDGET_MAX_AMOUNT, budgetGoal.maxAmount)
                put(DatabaseHelper.COLUMN_BUDGET_MONTH, budgetGoal.month)
                put(DatabaseHelper.COLUMN_BUDGET_YEAR, budgetGoal.year)
                put(DatabaseHelper.COLUMN_BUDGET_USER_ID, budgetGoal.userId)
            }

            val result = db.insertWithOnConflict(
                DatabaseHelper.TABLE_BUDGET_GOALS,
                null,
                values,
                android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
            )
            result != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun getBudgetGoalsForMonth(userId: Int, month: Int, year: Int): List<BudgetGoal> {
        val budgetGoals = mutableListOf<BudgetGoal>()
        val db = dbHelper.readableDatabase

        try {
            val query = """
                SELECT bg.*, c.${DatabaseHelper.COLUMN_CATEGORY_NAME}, 
                       c.${DatabaseHelper.COLUMN_CATEGORY_COLOR}, 
                       c.${DatabaseHelper.COLUMN_CATEGORY_ICON}
                FROM ${DatabaseHelper.TABLE_BUDGET_GOALS} bg
                LEFT JOIN ${DatabaseHelper.TABLE_CATEGORIES} c 
                ON bg.${DatabaseHelper.COLUMN_BUDGET_CATEGORY_ID} = c.${DatabaseHelper.COLUMN_CATEGORY_ID}
                WHERE bg.${DatabaseHelper.COLUMN_BUDGET_USER_ID} = ?
                AND bg.${DatabaseHelper.COLUMN_BUDGET_MONTH} = ?
                AND bg.${DatabaseHelper.COLUMN_BUDGET_YEAR} = ?
                ORDER BY bg.${DatabaseHelper.COLUMN_BUDGET_CATEGORY_ID} IS NULL DESC, c.${DatabaseHelper.COLUMN_CATEGORY_NAME}
            """.trimIndent()

            val cursor: Cursor = db.rawQuery(query, arrayOf(userId.toString(), month.toString(), year.toString()))

            if (cursor.moveToFirst()) {
                do {
                    val categoryIdIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_CATEGORY_ID)
                    val categoryId = if (cursor.isNull(categoryIdIndex)) null else cursor.getInt(categoryIdIndex)

                    val budgetGoal = BudgetGoal(
                        budgetId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_ID)),
                        categoryId = categoryId,
                        minAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_MIN_AMOUNT)),
                        maxAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_MAX_AMOUNT)),
                        month = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_MONTH)),
                        year = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_YEAR)),
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_USER_ID)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                    )

                    if (categoryId != null) {
                        val nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME)
                        budgetGoal.categoryName = if (cursor.isNull(nameIndex)) "Unknown" else cursor.getString(nameIndex)

                        val colorIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_COLOR)
                        budgetGoal.categoryColor = if (cursor.isNull(colorIndex)) "#8B7BA8" else cursor.getString(colorIndex)

                        val iconIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ICON)
                        budgetGoal.categoryIcon = if (cursor.isNull(iconIndex)) "budget" else cursor.getString(iconIndex)
                    }

                    budgetGoals.add(budgetGoal)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return budgetGoals
    }

    fun getOverallBudgetGoal(userId: Int, month: Int, year: Int): BudgetGoal? {
        val db = dbHelper.readableDatabase
        var budgetGoal: BudgetGoal? = null

        try {
            val cursor: Cursor = db.query(
                DatabaseHelper.TABLE_BUDGET_GOALS,
                null,
                "${DatabaseHelper.COLUMN_BUDGET_CATEGORY_ID} IS NULL AND ${DatabaseHelper.COLUMN_BUDGET_USER_ID} = ? AND ${DatabaseHelper.COLUMN_BUDGET_MONTH} = ? AND ${DatabaseHelper.COLUMN_BUDGET_YEAR} = ?",
                arrayOf(userId.toString(), month.toString(), year.toString()),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                budgetGoal = BudgetGoal(
                    budgetId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_ID)),
                    categoryId = null,
                    minAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_MIN_AMOUNT)),
                    maxAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_MAX_AMOUNT)),
                    month = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_MONTH)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_YEAR)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_USER_ID)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                )
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return budgetGoal
    }

    fun getCategoryBudgetGoal(userId: Int, categoryId: Int, month: Int, year: Int): BudgetGoal? {
        val db = dbHelper.readableDatabase
        var budgetGoal: BudgetGoal? = null

        try {
            val cursor: Cursor = db.query(
                DatabaseHelper.TABLE_BUDGET_GOALS,
                null,
                "${DatabaseHelper.COLUMN_BUDGET_CATEGORY_ID} = ? AND ${DatabaseHelper.COLUMN_BUDGET_USER_ID} = ? AND ${DatabaseHelper.COLUMN_BUDGET_MONTH} = ? AND ${DatabaseHelper.COLUMN_BUDGET_YEAR} = ?",
                arrayOf(categoryId.toString(), userId.toString(), month.toString(), year.toString()),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                budgetGoal = BudgetGoal(
                    budgetId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_ID)),
                    categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_CATEGORY_ID)),
                    minAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_MIN_AMOUNT)),
                    maxAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_MAX_AMOUNT)),
                    month = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_MONTH)),
                    year = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_YEAR)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BUDGET_USER_ID)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                )
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return budgetGoal
    }

    fun deleteBudgetGoal(budgetId: Int): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val result = db.delete(
                DatabaseHelper.TABLE_BUDGET_GOALS,
                "${DatabaseHelper.COLUMN_BUDGET_ID} = ?",
                arrayOf(budgetId.toString())
            )
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }
}
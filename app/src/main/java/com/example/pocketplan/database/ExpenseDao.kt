package com.pocketplan.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.pocketplan.models.Expense

class ExpenseDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addExpense(expense: Expense): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, expense.amount)
                put(DatabaseHelper.COLUMN_EXPENSE_DATE, expense.date)
                put(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION, expense.description)
                put(DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID, expense.categoryId)
                put(DatabaseHelper.COLUMN_EXPENSE_PHOTO_PATH, expense.photoPath)
                put(DatabaseHelper.COLUMN_EXPENSE_USER_ID, expense.userId)
            }

            val result = db.insert(DatabaseHelper.TABLE_EXPENSES, null, values)
            result != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun getAllExpenses(userId: Int): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = dbHelper.readableDatabase

        try {
            val query = """
                SELECT e.*, c.${DatabaseHelper.COLUMN_CATEGORY_NAME}, 
                       c.${DatabaseHelper.COLUMN_CATEGORY_COLOR}, 
                       c.${DatabaseHelper.COLUMN_CATEGORY_ICON}
                FROM ${DatabaseHelper.TABLE_EXPENSES} e
                INNER JOIN ${DatabaseHelper.TABLE_CATEGORIES} c 
                ON e.${DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID} = c.${DatabaseHelper.COLUMN_CATEGORY_ID}
                WHERE e.${DatabaseHelper.COLUMN_EXPENSE_USER_ID} = ?
                ORDER BY e.${DatabaseHelper.COLUMN_EXPENSE_DATE} DESC
            """.trimIndent()

            val cursor: Cursor = db.rawQuery(query, arrayOf(userId.toString()))

            if (cursor.moveToFirst()) {
                do {
                    val expense = Expense(
                        expenseId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_ID)),
                        amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT)),
                        date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DATE)),
                        description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION)),
                        categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID)),
                        photoPath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_PHOTO_PATH)),
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_USER_ID)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                    )

                    expense.categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME))
                    expense.categoryColor = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_COLOR))
                    expense.categoryIcon = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ICON))

                    expenses.add(expense)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return expenses
    }

    fun getExpensesByDateRange(userId: Int, startDate: String, endDate: String): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = dbHelper.readableDatabase

        try {
            val query = """
                SELECT e.*, c.${DatabaseHelper.COLUMN_CATEGORY_NAME}, 
                       c.${DatabaseHelper.COLUMN_CATEGORY_COLOR}, 
                       c.${DatabaseHelper.COLUMN_CATEGORY_ICON}
                FROM ${DatabaseHelper.TABLE_EXPENSES} e
                INNER JOIN ${DatabaseHelper.TABLE_CATEGORIES} c 
                ON e.${DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID} = c.${DatabaseHelper.COLUMN_CATEGORY_ID}
                WHERE e.${DatabaseHelper.COLUMN_EXPENSE_USER_ID} = ?
                AND e.${DatabaseHelper.COLUMN_EXPENSE_DATE} BETWEEN ? AND ?
                ORDER BY e.${DatabaseHelper.COLUMN_EXPENSE_DATE} DESC
            """.trimIndent()

            val cursor: Cursor = db.rawQuery(query, arrayOf(userId.toString(), startDate, endDate))

            if (cursor.moveToFirst()) {
                do {
                    val expense = Expense(
                        expenseId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_ID)),
                        amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT)),
                        date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DATE)),
                        description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DESCRIPTION)),
                        categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID)),
                        photoPath = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_PHOTO_PATH)),
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_USER_ID)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                    )

                    expense.categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME))
                    expense.categoryColor = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_COLOR))
                    expense.categoryIcon = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ICON))

                    expenses.add(expense)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return expenses
    }

    fun deleteExpense(expenseId: Int): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val result = db.delete(
                DatabaseHelper.TABLE_EXPENSES,
                "${DatabaseHelper.COLUMN_EXPENSE_ID} = ?",
                arrayOf(expenseId.toString())
            )
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun getTotalExpenses(userId: Int): Double {
        val db = dbHelper.readableDatabase
        var total = 0.0

        try {
            val cursor = db.rawQuery(
                "SELECT SUM(${DatabaseHelper.COLUMN_EXPENSE_AMOUNT}) FROM ${DatabaseHelper.TABLE_EXPENSES} WHERE ${DatabaseHelper.COLUMN_EXPENSE_USER_ID} = ?",
                arrayOf(userId.toString())
            )

            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0)
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return total
    }

    fun getCategorySpending(userId: Int, startDate: String, endDate: String): List<com.pocketplan.models.CategorySpending> {
        val categorySpendingList = mutableListOf<com.pocketplan.models.CategorySpending>()
        val db = dbHelper.readableDatabase

        try {
            val query = """
                SELECT 
                    c.${DatabaseHelper.COLUMN_CATEGORY_ID},
                    c.${DatabaseHelper.COLUMN_CATEGORY_NAME},
                    c.${DatabaseHelper.COLUMN_CATEGORY_COLOR},
                    c.${DatabaseHelper.COLUMN_CATEGORY_ICON},
                    SUM(e.${DatabaseHelper.COLUMN_EXPENSE_AMOUNT}) as total_spent,
                    COUNT(e.${DatabaseHelper.COLUMN_EXPENSE_ID}) as expense_count
                FROM ${DatabaseHelper.TABLE_EXPENSES} e
                INNER JOIN ${DatabaseHelper.TABLE_CATEGORIES} c 
                ON e.${DatabaseHelper.COLUMN_EXPENSE_CATEGORY_ID} = c.${DatabaseHelper.COLUMN_CATEGORY_ID}
                WHERE e.${DatabaseHelper.COLUMN_EXPENSE_USER_ID} = ?
                AND e.${DatabaseHelper.COLUMN_EXPENSE_DATE} BETWEEN ? AND ?
                GROUP BY c.${DatabaseHelper.COLUMN_CATEGORY_ID}
                ORDER BY total_spent DESC
            """.trimIndent()

            val cursor = db.rawQuery(query, arrayOf(userId.toString(), startDate, endDate))

            if (cursor.moveToFirst()) {
                do {
                    val categorySpending = com.pocketplan.models.CategorySpending(
                        categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)),
                        categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME)),
                        categoryColor = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_COLOR)),
                        categoryIcon = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ICON)),
                        totalSpent = cursor.getDouble(cursor.getColumnIndexOrThrow("total_spent")),
                        expenseCount = cursor.getInt(cursor.getColumnIndexOrThrow("expense_count"))
                    )
                    categorySpendingList.add(categorySpending)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return categorySpendingList
    }

    fun getDailySpending(userId: Int, startDate: String, endDate: String): List<com.pocketplan.models.DailySpending> {
        val dailySpendingList = mutableListOf<com.pocketplan.models.DailySpending>()
        val db = dbHelper.readableDatabase

        try {
            val query = """
                SELECT 
                    ${DatabaseHelper.COLUMN_EXPENSE_DATE} as date,
                    SUM(${DatabaseHelper.COLUMN_EXPENSE_AMOUNT}) as total_amount,
                    COUNT(${DatabaseHelper.COLUMN_EXPENSE_ID}) as expense_count
                FROM ${DatabaseHelper.TABLE_EXPENSES}
                WHERE ${DatabaseHelper.COLUMN_EXPENSE_USER_ID} = ?
                AND ${DatabaseHelper.COLUMN_EXPENSE_DATE} BETWEEN ? AND ?
                GROUP BY ${DatabaseHelper.COLUMN_EXPENSE_DATE}
                ORDER BY ${DatabaseHelper.COLUMN_EXPENSE_DATE} ASC
            """.trimIndent()

            val cursor = db.rawQuery(query, arrayOf(userId.toString(), startDate, endDate))

            if (cursor.moveToFirst()) {
                do {
                    val dailySpending = com.pocketplan.models.DailySpending(
                        date = cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")),
                        expenseCount = cursor.getInt(cursor.getColumnIndexOrThrow("expense_count"))
                    )
                    dailySpendingList.add(dailySpending)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return dailySpendingList
    }
}
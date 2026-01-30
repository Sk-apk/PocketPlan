package com.pocketplan.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.pocketplan.models.Category

class CategoryDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addCategory(category: Category): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.categoryName)
                put(DatabaseHelper.COLUMN_CATEGORY_COLOR, category.categoryColor)
                put(DatabaseHelper.COLUMN_CATEGORY_ICON, category.categoryIcon)
                put(DatabaseHelper.COLUMN_CATEGORY_USER_ID, category.userId)
            }

            val result = db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values)
            result != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun getAllCategories(userId: Int): List<Category> {
        val categories = mutableListOf<Category>()
        val db = dbHelper.readableDatabase

        try {
            val cursor: Cursor = db.query(
                DatabaseHelper.TABLE_CATEGORIES,
                null,
                "${DatabaseHelper.COLUMN_CATEGORY_USER_ID} = ?",
                arrayOf(userId.toString()),
                null, null,
                "${DatabaseHelper.COLUMN_CREATED_AT} DESC"
            )

            if (cursor.moveToFirst()) {
                do {
                    val category = Category(
                        categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID)),
                        categoryName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME)),
                        categoryColor = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_COLOR)),
                        categoryIcon = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ICON)),
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_USER_ID)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                    )
                    categories.add(category)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return categories
    }

    fun deleteCategory(categoryId: Int): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val result = db.delete(
                DatabaseHelper.TABLE_CATEGORIES,
                "${DatabaseHelper.COLUMN_CATEGORY_ID} = ?",
                arrayOf(categoryId.toString())
            )
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun updateCategory(category: Category): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_CATEGORY_NAME, category.categoryName)
                put(DatabaseHelper.COLUMN_CATEGORY_COLOR, category.categoryColor)
                put(DatabaseHelper.COLUMN_CATEGORY_ICON, category.categoryIcon)
            }

            val result = db.update(
                DatabaseHelper.TABLE_CATEGORIES,
                values,
                "${DatabaseHelper.COLUMN_CATEGORY_ID} = ?",
                arrayOf(category.categoryId.toString())
            )
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun isCategoryNameExists(categoryName: String, userId: Int): Boolean {
        val db = dbHelper.readableDatabase
        var exists = false

        try {
            val cursor = db.query(
                DatabaseHelper.TABLE_CATEGORIES,
                arrayOf(DatabaseHelper.COLUMN_CATEGORY_ID),
                "${DatabaseHelper.COLUMN_CATEGORY_NAME} = ? AND ${DatabaseHelper.COLUMN_CATEGORY_USER_ID} = ?",
                arrayOf(categoryName, userId.toString()),
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
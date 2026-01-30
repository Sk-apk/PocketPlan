package com.pocketplan.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.pocketplan.models.Loan

class LoanDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addLoan(loan: Loan): Long {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_LOAN_NAME, loan.loanName)
                put(DatabaseHelper.COLUMN_LOAN_PRINCIPAL, loan.principalAmount)
                put(DatabaseHelper.COLUMN_LOAN_INTEREST_RATE, loan.interestRate)
                put(DatabaseHelper.COLUMN_LOAN_MIN_PAYMENT, loan.minimumPayment)
                put(DatabaseHelper.COLUMN_LOAN_CURRENT_BALANCE, loan.currentBalance)
                put(DatabaseHelper.COLUMN_LOAN_USER_ID, loan.userId)
            }

            db.insert(DatabaseHelper.TABLE_LOANS, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        } finally {
            db.close()
        }
    }

    fun getAllLoans(userId: Int): List<Loan> {
        val loans = mutableListOf<Loan>()
        val db = dbHelper.readableDatabase

        try {
            val cursor: Cursor = db.query(
                DatabaseHelper.TABLE_LOANS,
                null,
                "${DatabaseHelper.COLUMN_LOAN_USER_ID} = ?",
                arrayOf(userId.toString()),
                null, null,
                "${DatabaseHelper.COLUMN_CREATED_AT} DESC"
            )

            if (cursor.moveToFirst()) {
                do {
                    val loan = Loan(
                        loanId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_ID)),
                        loanName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_NAME)),
                        principalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_PRINCIPAL)),
                        interestRate = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_INTEREST_RATE)),
                        minimumPayment = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_MIN_PAYMENT)),
                        currentBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_CURRENT_BALANCE)),
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_USER_ID)),
                        createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                    )
                    loans.add(loan)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return loans
    }

    fun getLoanById(loanId: Int): Loan? {
        val db = dbHelper.readableDatabase
        var loan: Loan? = null

        try {
            val cursor: Cursor = db.query(
                DatabaseHelper.TABLE_LOANS,
                null,
                "${DatabaseHelper.COLUMN_LOAN_ID} = ?",
                arrayOf(loanId.toString()),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                loan = Loan(
                    loanId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_ID)),
                    loanName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_NAME)),
                    principalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_PRINCIPAL)),
                    interestRate = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_INTEREST_RATE)),
                    minimumPayment = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_MIN_PAYMENT)),
                    currentBalance = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_CURRENT_BALANCE)),
                    userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOAN_USER_ID)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT))
                )
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return loan
    }

    fun updateLoan(loan: Loan): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_LOAN_NAME, loan.loanName)
                put(DatabaseHelper.COLUMN_LOAN_PRINCIPAL, loan.principalAmount)
                put(DatabaseHelper.COLUMN_LOAN_INTEREST_RATE, loan.interestRate)
                put(DatabaseHelper.COLUMN_LOAN_MIN_PAYMENT, loan.minimumPayment)
                put(DatabaseHelper.COLUMN_LOAN_CURRENT_BALANCE, loan.currentBalance)
            }

            val result = db.update(
                DatabaseHelper.TABLE_LOANS,
                values,
                "${DatabaseHelper.COLUMN_LOAN_ID} = ?",
                arrayOf(loan.loanId.toString())
            )
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun deleteLoan(loanId: Int): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            val result = db.delete(
                DatabaseHelper.TABLE_LOANS,
                "${DatabaseHelper.COLUMN_LOAN_ID} = ?",
                arrayOf(loanId.toString())
            )
            result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun getTotalDebt(userId: Int): Double {
        val loans = getAllLoans(userId)
        return loans.sumOf { it.currentBalance }
    }
}
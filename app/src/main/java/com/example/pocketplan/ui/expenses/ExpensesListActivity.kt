package com.pocketplan.ui.expenses

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.database.ExpenseDao
import com.pocketplan.models.Expense
import com.pocketplan.utils.SessionManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ExpensesListActivity : AppCompatActivity() {

    private lateinit var tvDateRange: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnFilterStart: Button
    private lateinit var btnFilterEnd: Button
    private lateinit var btnShowAll: Button
    private lateinit var rvExpenses: RecyclerView

    private lateinit var expenseDao: ExpenseDao
    private lateinit var sessionManager: SessionManager
    private lateinit var expenseAdapter: ExpenseAdapter

    private var expenses = mutableListOf<Expense>()
    private var startDate: Calendar = Calendar.getInstance()
    private var endDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        supportActionBar?.title = "My Expenses"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        expenseDao = ExpenseDao(this)
        sessionManager = SessionManager(this)

        // Set default date range to current month
        startDate.set(Calendar.DAY_OF_MONTH, 1)
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH))

        setupRecyclerView()
        updateDateRangeDisplay()
        loadExpenses()

        btnFilterStart.setOnClickListener {
            showStartDatePicker()
        }

        btnFilterEnd.setOnClickListener {
            showEndDatePicker()
        }

        btnShowAll.setOnClickListener {
            loadAllExpenses()
        }
    }

    private fun initViews() {
        tvDateRange = findViewById(R.id.tvDateRange)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnFilterStart = findViewById(R.id.btnFilterStart)
        btnFilterEnd = findViewById(R.id.btnFilterEnd)
        btnShowAll = findViewById(R.id.btnShowAll)
        rvExpenses = findViewById(R.id.rvExpenses)
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(
            expenses = expenses,
            onItemClick = { expense ->
                if (expense.photoPath != null) {
                    showReceiptDialog(expense.photoPath!!)
                }
            },
            onDeleteClick = { expense ->
                deleteExpense(expense)
            }
        )

        rvExpenses.apply {
            layoutManager = LinearLayoutManager(this@ExpensesListActivity)
            adapter = expenseAdapter
        }
    }

    private fun updateDateRangeDisplay() {
        val start = displayDateFormat.format(startDate.time)
        val end = displayDateFormat.format(endDate.time)
        tvDateRange.text = "$start - $end"
    }

    private fun loadExpenses() {
        val userId = sessionManager.getUserId()
        val startDateStr = dateFormat.format(startDate.time)
        val endDateStr = dateFormat.format(endDate.time)

        expenses.clear()
        expenses.addAll(expenseDao.getExpensesByDateRange(userId, startDateStr, endDateStr))
        expenseAdapter.notifyDataSetChanged()

        updateTotalAmount()
    }

    private fun loadAllExpenses() {
        val userId = sessionManager.getUserId()
        expenses.clear()
        expenses.addAll(expenseDao.getAllExpenses(userId))
        expenseAdapter.notifyDataSetChanged()

        tvDateRange.text = "All Time"
        updateTotalAmount()
    }

    private fun updateTotalAmount() {
        val total = expenses.sumOf { it.amount }
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        tvTotalAmount.text = "Total: ${currencyFormat.format(total)}"
    }

    private fun showStartDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                startDate.set(year, month, dayOfMonth)
                updateDateRangeDisplay()
                loadExpenses()
            },
            startDate.get(Calendar.YEAR),
            startDate.get(Calendar.MONTH),
            startDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showEndDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                endDate.set(year, month, dayOfMonth)
                updateDateRangeDisplay()
                loadExpenses()
            },
            endDate.get(Calendar.YEAR),
            endDate.get(Calendar.MONTH),
            endDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showReceiptDialog(photoPath: String) {
        val dialog = ViewReceiptDialog(photoPath)
        dialog.show(supportFragmentManager, "ViewReceiptDialog")
    }

    private fun deleteExpense(expense: Expense) {
        val success = expenseDao.deleteExpense(expense.expenseId)
        if (success) {
            loadExpenses()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
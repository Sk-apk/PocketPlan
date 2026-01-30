package com.pocketplan.ui.spending

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.database.ExpenseDao
import com.pocketplan.models.CategorySpending
import com.pocketplan.utils.SessionManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class CategorySpendingActivity : AppCompatActivity() {

    private lateinit var tvDateRange: TextView
    private lateinit var tvTotalSpent: TextView
    private lateinit var btnFilterStart: Button
    private lateinit var btnFilterEnd: Button
    private lateinit var btnShowAll: Button
    private lateinit var rvCategorySpending: RecyclerView

    private lateinit var expenseDao: ExpenseDao
    private lateinit var sessionManager: SessionManager
    private lateinit var categorySpendingAdapter: CategorySpendingAdapter

    private var categorySpendingList = mutableListOf<CategorySpending>()
    private var startDate: Calendar = Calendar.getInstance()
    private var endDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_spending)

        supportActionBar?.title = "Spending by Category"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        expenseDao = ExpenseDao(this)
        sessionManager = SessionManager(this)

        // Set default date range to current month
        startDate.set(Calendar.DAY_OF_MONTH, 1)
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH))

        setupRecyclerView()
        updateDateRangeDisplay()
        loadCategorySpending()

        btnFilterStart.setOnClickListener {
            showStartDatePicker()
        }

        btnFilterEnd.setOnClickListener {
            showEndDatePicker()
        }

        btnShowAll.setOnClickListener {
            loadAllTimeSpending()
        }
    }

    private fun initViews() {
        tvDateRange = findViewById(R.id.tvDateRange)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        btnFilterStart = findViewById(R.id.btnFilterStart)
        btnFilterEnd = findViewById(R.id.btnFilterEnd)
        btnShowAll = findViewById(R.id.btnShowAll)
        rvCategorySpending = findViewById(R.id.rvCategorySpending)
    }

    private fun setupRecyclerView() {
        categorySpendingAdapter = CategorySpendingAdapter(categorySpendingList)

        rvCategorySpending.apply {
            layoutManager = LinearLayoutManager(this@CategorySpendingActivity)
            adapter = categorySpendingAdapter
        }
    }

    private fun updateDateRangeDisplay() {
        val start = displayDateFormat.format(startDate.time)
        val end = displayDateFormat.format(endDate.time)
        tvDateRange.text = "$start - $end"
    }

    private fun loadCategorySpending() {
        val userId = sessionManager.getUserId()
        val startDateStr = dateFormat.format(startDate.time)
        val endDateStr = dateFormat.format(endDate.time)

        categorySpendingList.clear()
        val spendingData = expenseDao.getCategorySpending(userId, startDateStr, endDateStr)

        // Calculate percentages
        val total = spendingData.sumOf { it.totalSpent }
        spendingData.forEach { spending ->
            spending.percentage = if (total > 0) (spending.totalSpent / total) * 100 else 0.0
        }

        categorySpendingList.addAll(spendingData)
        categorySpendingAdapter.notifyDataSetChanged()

        updateTotalSpent(total)
    }

    private fun loadAllTimeSpending() {
        val userId = sessionManager.getUserId()

        // Set date range to cover all time
        val minDate = "1970-01-01"
        val maxDate = "2099-12-31"

        categorySpendingList.clear()
        val spendingData = expenseDao.getCategorySpending(userId, minDate, maxDate)

        // Calculate percentages
        val total = spendingData.sumOf { it.totalSpent }
        spendingData.forEach { spending ->
            spending.percentage = if (total > 0) (spending.totalSpent / total) * 100 else 0.0
        }

        categorySpendingList.addAll(spendingData)
        categorySpendingAdapter.notifyDataSetChanged()

        tvDateRange.text = "All Time"
        updateTotalSpent(total)
    }

    private fun updateTotalSpent(total: Double) {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        tvTotalSpent.text = "Total: ${currencyFormat.format(total)}"
    }

    private fun showStartDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                startDate.set(year, month, dayOfMonth)
                updateDateRangeDisplay()
                loadCategorySpending()
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
                loadCategorySpending()
            },
            endDate.get(Calendar.YEAR),
            endDate.get(Calendar.MONTH),
            endDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
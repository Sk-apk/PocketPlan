package com.pocketplan.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.database.BudgetGoalDao
import com.pocketplan.database.ExpenseDao
import com.pocketplan.models.BudgetProgress
import com.pocketplan.models.BudgetStatus
import com.pocketplan.utils.SessionManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvMonth: TextView
    private lateinit var tvOverallStatus: TextView
    private lateinit var tvTotalBudget: TextView
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var tvWarningMessage: TextView
    private lateinit var rvBudgetProgress: RecyclerView

    private lateinit var budgetGoalDao: BudgetGoalDao
    private lateinit var expenseDao: ExpenseDao
    private lateinit var sessionManager: SessionManager
    private lateinit var budgetProgressAdapter: BudgetProgressAdapter

    private var budgetProgressList = mutableListOf<BudgetProgress>()
    private val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportActionBar?.title = "Budget Dashboard"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        budgetGoalDao = BudgetGoalDao(this)
        expenseDao = ExpenseDao(this)
        sessionManager = SessionManager(this)

        setupRecyclerView()
        loadDashboard()
    }

    private fun initViews() {
        tvMonth = findViewById(R.id.tvMonth)
        tvOverallStatus = findViewById(R.id.tvOverallStatus)
        tvTotalBudget = findViewById(R.id.tvTotalBudget)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        tvRemaining = findViewById(R.id.tvRemaining)
        tvWarningMessage = findViewById(R.id.tvWarningMessage)
        rvBudgetProgress = findViewById(R.id.rvBudgetProgress)

        // Set current month
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonth.text = monthFormat.format(calendar.time)
    }

    private fun setupRecyclerView() {
        budgetProgressAdapter = BudgetProgressAdapter(budgetProgressList)

        rvBudgetProgress.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = budgetProgressAdapter
        }
    }

    private fun loadDashboard() {
        val userId = sessionManager.getUserId()
        val budgetGoals = budgetGoalDao.getBudgetGoalsForMonth(userId, currentMonth, currentYear)

        if (budgetGoals.isEmpty()) {
            tvWarningMessage.visibility = View.VISIBLE
            tvWarningMessage.text = "⚠️ No budget goals set for this month. Set your goals to track progress!"
            return
        }

        tvWarningMessage.visibility = View.GONE

        // Get date range for current month
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        // Get spending by category
        val categorySpending = expenseDao.getCategorySpending(userId, startDate, endDate)
        val spendingMap = categorySpending.associateBy { it.categoryId }

        budgetProgressList.clear()

        var totalBudgetMax = 0.0
        var totalSpent = 0.0
        var hasOverspending = false

        budgetGoals.forEach { goal ->
            val spent = spendingMap[goal.categoryId]?.totalSpent ?: 0.0
            totalSpent += spent
            totalBudgetMax += goal.maxAmount

            val progress = BudgetProgress(
                categoryId = goal.categoryId,
                categoryName = goal.categoryName,
                categoryColor = goal.categoryColor,
                categoryIcon = goal.categoryIcon,
                minBudget = goal.minAmount,
                maxBudget = goal.maxAmount,
                actualSpent = spent
            )

            // Calculate progress percentage (based on max budget)
            progress.progressPercentage = if (goal.maxAmount > 0) {
                (spent / goal.maxAmount) * 100
            } else {
                0.0
            }

            // Determine status
            progress.status = when {
                spent < goal.minAmount -> BudgetStatus.UNDER_MIN
                spent <= goal.maxAmount -> BudgetStatus.ON_TRACK
                else -> {
                    hasOverspending = true
                    BudgetStatus.OVER_MAX
                }
            }

            budgetProgressList.add(progress)
        }

        budgetProgressAdapter.notifyDataSetChanged()

        // Update summary
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        tvTotalBudget.text = currencyFormat.format(totalBudgetMax)
        tvTotalSpent.text = currencyFormat.format(totalSpent)

        val remaining = totalBudgetMax - totalSpent
        tvRemaining.text = currencyFormat.format(if (remaining > 0) remaining else 0.0)

        // Update overall status
        when {
            hasOverspending -> {
                tvOverallStatus.text = "⚠️ Over Budget"
                tvOverallStatus.setTextColor(getColor(R.color.error))
                tvWarningMessage.visibility = View.VISIBLE
                tvWarningMessage.text = "⚠️ You are overspending in some categories! Review your expenses."
                tvWarningMessage.setTextColor(getColor(R.color.error))
            }
            totalSpent > totalBudgetMax * 0.8 -> {
                tvOverallStatus.text = "⚡ Approaching Limit"
                tvOverallStatus.setTextColor(getColor(R.color.warning))
                tvWarningMessage.visibility = View.VISIBLE
                tvWarningMessage.text = "⚡ You've used 80%+ of your budget. Watch your spending!"
                tvWarningMessage.setTextColor(getColor(R.color.warning))
            }
            else -> {
                tvOverallStatus.text = "✅ On Track"
                tvOverallStatus.setTextColor(getColor(R.color.success))
                tvWarningMessage.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }
}
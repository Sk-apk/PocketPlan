package com.pocketplan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pocketplan.R
import com.pocketplan.ui.gamification.AchievementsActivity
import com.pocketplan.ui.analytics.AnalyticsActivity
import com.pocketplan.ui.spending.CategorySpendingActivity
import com.pocketplan.ui.budget.BudgetGoalsActivity
import com.pocketplan.ui.categories.CategoriesActivity
import com.pocketplan.ui.dashboard.DashboardActivity
import com.pocketplan.ui.expenses.AddExpenseActivity
import com.pocketplan.ui.expenses.ExpensesListActivity
import com.pocketplan.ui.loans.LoanPlannerActivity
import com.pocketplan.ui.shared.SharedBudgetActivity
import com.pocketplan.utils.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnCategories: Button
    private lateinit var btnAddExpense: Button
    private lateinit var btnExpensesList: Button
    private lateinit var btnBudgetGoals: Button
    private lateinit var btnDashboard: Button
    private lateinit var btnAnalytics: Button
    private lateinit var btnCategorySpending: Button
    private lateinit var btnAchievements: Button
    private lateinit var btnSharedBudgets: Button
    private lateinit var btnLoanPlanner: Button
    private lateinit var btnLogout: Button

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "PocketPlan"

        sessionManager = SessionManager(this)

        initViews()
        setupClickListeners()

        // Display welcome message
        val username = sessionManager.getUsername()
        tvWelcome.text = "Welcome back, $username! 👋"
    }

    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        btnCategories = findViewById(R.id.btnCategories)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnExpensesList = findViewById(R.id.btnExpensesList)
        btnBudgetGoals = findViewById(R.id.btnBudgetGoals)
        btnDashboard = findViewById(R.id.btnDashboard)
        btnAnalytics = findViewById(R.id.btnAnalytics)
        btnCategorySpending = findViewById(R.id.btnCategorySpending)
        btnAchievements = findViewById(R.id.btnAchievements)
        btnSharedBudgets = findViewById(R.id.btnSharedBudgets)
        btnLoanPlanner = findViewById(R.id.btnLoanPlanner)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupClickListeners() {
        btnCategories.setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }

        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        btnExpensesList.setOnClickListener {
            startActivity(Intent(this, ExpensesListActivity::class.java))
        }

        btnBudgetGoals.setOnClickListener {
            startActivity(Intent(this, BudgetGoalsActivity::class.java))
        }

        btnDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        btnAnalytics.setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
        }

        btnCategorySpending.setOnClickListener {
            startActivity(Intent(this, CategorySpendingActivity::class.java))
        }

        btnAchievements.setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }

        //  Shared Budgeting
        btnSharedBudgets.setOnClickListener {
            startActivity(Intent(this, SharedBudgetActivity::class.java))
        }

        //Loan Planner
        btnLoanPlanner.setOnClickListener {
            startActivity(Intent(this, LoanPlannerActivity::class.java))
        }

        btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, com.pocketplan.ui.auth.LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
package com.pocketplan.ui.budget

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.database.BudgetGoalDao
import com.pocketplan.database.CategoryDao
import com.pocketplan.models.BudgetGoal
import com.pocketplan.models.Category
import com.pocketplan.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*
import com.pocketplan.ui.budget.SetOverallBudgetDialog

class BudgetGoalsActivity : AppCompatActivity() {

    private lateinit var tvMonth: TextView
    private lateinit var btnSetOverall: Button
    private lateinit var btnSetCategory: Button
    private lateinit var rvBudgetGoals: RecyclerView

    private lateinit var budgetGoalDao: BudgetGoalDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var sessionManager: SessionManager
    private lateinit var budgetGoalAdapter: BudgetGoalAdapter

    private var budgetGoals = mutableListOf<BudgetGoal>()
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_goals)

        supportActionBar?.title = "Budget Goals"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        budgetGoalDao = BudgetGoalDao(this)
        categoryDao = CategoryDao(this)
        sessionManager = SessionManager(this)

        setupRecyclerView()
        updateMonthDisplay()
        loadBudgetGoals()

        btnSetOverall.setOnClickListener {
            showSetOverallBudgetDialog()
        }

        btnSetCategory.setOnClickListener {
            showSetCategoryBudgetDialog()
        }
    }

    private fun initViews() {
        tvMonth = findViewById(R.id.tvMonth)
        btnSetOverall = findViewById(R.id.btnSetOverall)
        btnSetCategory = findViewById(R.id.btnSetCategory)
        rvBudgetGoals = findViewById(R.id.rvBudgetGoals)
    }

    private fun setupRecyclerView() {
        budgetGoalAdapter = BudgetGoalAdapter(
            budgetGoals = budgetGoals,
            onDeleteClick = { budgetGoal ->
                deleteBudgetGoal(budgetGoal)
            }
        )

        rvBudgetGoals.apply {
            layoutManager = LinearLayoutManager(this@BudgetGoalsActivity)
            adapter = budgetGoalAdapter
        }
    }

    private fun updateMonthDisplay() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, currentMonth - 1)
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonth.text = monthFormat.format(calendar.time)
    }

    private fun loadBudgetGoals() {
        val userId = sessionManager.getUserId()
        budgetGoals.clear()
        budgetGoals.addAll(budgetGoalDao.getBudgetGoalsForMonth(userId, currentMonth, currentYear))
        budgetGoalAdapter.notifyDataSetChanged()
    }

    private fun showSetOverallBudgetDialog() {
        val userId = sessionManager.getUserId()
        val existingGoal = budgetGoalDao.getOverallBudgetGoal(userId, currentMonth, currentYear)

        val dialog = SetOverallBudgetDialog(existingGoal) { minAmount, maxAmount ->
            saveOverallBudget(minAmount, maxAmount)
        }
        dialog.show(supportFragmentManager, "SetOverallBudgetDialog")
    }

    private fun showSetCategoryBudgetDialog() {
        val userId = sessionManager.getUserId()
        val categories = categoryDao.getAllCategories(userId)

        if (categories.isEmpty()) {
            Toast.makeText(this, "Please create categories first!", Toast.LENGTH_SHORT).show()
            return
        }

        val dialog = SetCategoryBudgetDialog(categories) { category, minAmount, maxAmount ->
            saveCategoryBudget(category, minAmount, maxAmount)
        }
        dialog.show(supportFragmentManager, "SetCategoryBudgetDialog")
    }

    private fun saveOverallBudget(minAmount: Double, maxAmount: Double) {
        val userId = sessionManager.getUserId()

        val budgetGoal = BudgetGoal(
            categoryId = null,
            minAmount = minAmount,
            maxAmount = maxAmount,
            month = currentMonth,
            year = currentYear,
            userId = userId
        )

        val success = budgetGoalDao.setBudgetGoal(budgetGoal)
        if (success) {
            Toast.makeText(this, "Overall budget goal set!", Toast.LENGTH_SHORT).show()
            loadBudgetGoals()
        } else {
            Toast.makeText(this, "Failed to set budget goal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCategoryBudget(category: Category, minAmount: Double, maxAmount: Double) {
        val userId = sessionManager.getUserId()

        val budgetGoal = BudgetGoal(
            categoryId = category.categoryId,
            minAmount = minAmount,
            maxAmount = maxAmount,
            month = currentMonth,
            year = currentYear,
            userId = userId,
            categoryName = category.categoryName,
            categoryColor = category.categoryColor,
            categoryIcon = category.categoryIcon
        )

        val success = budgetGoalDao.setBudgetGoal(budgetGoal)
        if (success) {
            Toast.makeText(this, "Category budget goal set!", Toast.LENGTH_SHORT).show()
            loadBudgetGoals()
        } else {
            Toast.makeText(this, "Failed to set budget goal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteBudgetGoal(budgetGoal: BudgetGoal) {
        val success = budgetGoalDao.deleteBudgetGoal(budgetGoal.budgetId)
        if (success) {
            Toast.makeText(this, "Budget goal deleted", Toast.LENGTH_SHORT).show()
            loadBudgetGoals()
        } else {
            Toast.makeText(this, "Failed to delete budget goal", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
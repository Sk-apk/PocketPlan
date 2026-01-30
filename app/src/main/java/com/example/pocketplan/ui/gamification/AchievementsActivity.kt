package com.pocketplan.ui.gamification

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.database.*
import com.pocketplan.models.Achievement
import com.pocketplan.models.AchievementType
import com.pocketplan.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class AchievementsActivity : AppCompatActivity() {

    private lateinit var tvUnlockedCount: TextView
    private lateinit var tvProgress: TextView
    private lateinit var rvAchievements: RecyclerView

    private lateinit var achievementDao: AchievementDao
    private lateinit var expenseDao: ExpenseDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var budgetGoalDao: BudgetGoalDao
    private lateinit var sessionManager: SessionManager
    private lateinit var achievementAdapter: AchievementAdapter

    private var achievements = mutableListOf<Achievement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        supportActionBar?.title = "Achievements"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        achievementDao = AchievementDao(this)
        expenseDao = ExpenseDao(this)
        categoryDao = CategoryDao(this)
        budgetGoalDao = BudgetGoalDao(this)
        sessionManager = SessionManager(this)

        setupRecyclerView()
        checkAndUnlockAchievements()
        loadAchievements()
    }

    private fun initViews() {
        tvUnlockedCount = findViewById(R.id.tvUnlockedCount)
        tvProgress = findViewById(R.id.tvProgress)
        rvAchievements = findViewById(R.id.rvAchievements)
    }

    private fun setupRecyclerView() {
        achievementAdapter = AchievementAdapter(achievements)

        rvAchievements.apply {
            layoutManager = GridLayoutManager(this@AchievementsActivity, 2)
            adapter = achievementAdapter
        }
    }

    private fun checkAndUnlockAchievements() {
        val userId = sessionManager.getUserId()

        // Check FIRST_EXPENSE
        val allExpenses = expenseDao.getAllExpenses(userId)
        if (allExpenses.isNotEmpty()) {
            achievementDao.unlockAchievement(userId, AchievementType.FIRST_EXPENSE.name)
        }

        // Check EXPENSE_100
        if (allExpenses.size >= 100) {
            achievementDao.unlockAchievement(userId, AchievementType.EXPENSE_100.name)
        }

        // Check RECEIPTS_10
        val expensesWithPhotos = allExpenses.count { it.photoPath != null }
        if (expensesWithPhotos >= 10) {
            achievementDao.unlockAchievement(userId, AchievementType.RECEIPTS_10.name)
        }

        // Check CATEGORY_MASTER
        val categories = categoryDao.getAllCategories(userId)
        if (categories.size >= 5) {
            achievementDao.unlockAchievement(userId, AchievementType.CATEGORY_MASTER.name)
        }

        // Check BUDGET_SET
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val budgetGoals = budgetGoalDao.getBudgetGoalsForMonth(userId, currentMonth, currentYear)
        if (budgetGoals.isNotEmpty()) {
            achievementDao.unlockAchievement(userId, AchievementType.BUDGET_SET.name)
        }

        // Check BUDGET_MET
        if (budgetGoals.isNotEmpty()) {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            val categorySpending = expenseDao.getCategorySpending(userId, startDate, endDate)
            val spendingMap = categorySpending.associateBy { it.categoryId }

            var allGoalsMet = true
            budgetGoals.forEach { goal ->
                val spent = spendingMap[goal.categoryId]?.totalSpent ?: 0.0
                if (spent > goal.maxAmount) {
                    allGoalsMet = false
                }
            }

            if (allGoalsMet) {
                achievementDao.unlockAchievement(userId, AchievementType.BUDGET_MET.name)
            }
        }
    }

    private fun loadAchievements() {
        val userId = sessionManager.getUserId()
        val unlockedAchievements = achievementDao.getUserAchievements(userId)
        val unlockedTypes = unlockedAchievements.map { it.achievementType }.toSet()
        val unlockedDates = unlockedAchievements.associateBy({ it.achievementType }, { it.achievementDate })

        achievements.clear()
        achievements.addAll(getAllAchievements().map { achievement ->
            achievement.copy(
                isUnlocked = unlockedTypes.contains(achievement.type.name),
                unlockedDate = unlockedDates[achievement.type.name]
            )
        })

        achievementAdapter.notifyDataSetChanged()

        // Update progress
        val unlockedCount = achievements.count { it.isUnlocked }
        val totalCount = achievements.size
        tvUnlockedCount.text = "$unlockedCount / $totalCount"
        tvProgress.text = "${(unlockedCount * 100 / totalCount)}% Complete"
    }

    private fun getAllAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                AchievementType.FIRST_EXPENSE,
                "First Step",
                "Add your first expense",
                "🎯",
                "Log 1 expense"
            ),
            Achievement(
                AchievementType.EXPENSE_STREAK_7,
                "Weekly Warrior",
                "Log expenses 7 days in a row",
                "🔥",
                "7 day streak"
            ),
            Achievement(
                AchievementType.EXPENSE_STREAK_30,
                "Monthly Master",
                "Log expenses 30 days in a row",
                "⚡",
                "30 day streak"
            ),
            Achievement(
                AchievementType.BUDGET_SET,
                "Goal Setter",
                "Set your first budget goal",
                "🎯",
                "Create 1 budget goal"
            ),
            Achievement(
                AchievementType.BUDGET_MET,
                "On Track",
                "Meet your budget goals for a month",
                "✅",
                "Stay within budget"
            ),
            Achievement(
                AchievementType.BUDGET_MET_3,
                "Budget Boss",
                "Meet budget goals for 3 months",
                "👑",
                "3 months on budget"
            ),
            Achievement(
                AchievementType.UNDER_BUDGET,
                "Money Saver",
                "Finish a month under budget",
                "💰",
                "Spend less than max"
            ),
            Achievement(
                AchievementType.CATEGORY_MASTER,
                "Organizer",
                "Create 5 budget categories",
                "📁",
                "Create 5 categories"
            ),
            Achievement(
                AchievementType.EXPENSE_100,
                "Dedicated Tracker",
                "Log 100 expenses",
                "💯",
                "Log 100 expenses"
            ),
            Achievement(
                AchievementType.SAVER,
                "Super Saver",
                "Save 20% or more in a month",
                "🌟",
                "Save 20%+"
            ),
            Achievement(
                AchievementType.RECEIPTS_10,
                "Paper Trail",
                "Attach 10 receipt photos",
                "📸",
                "10 receipt photos"
            )
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
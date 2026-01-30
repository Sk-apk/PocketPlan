package com.pocketplan.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "PocketPlan.db"
        private const val DATABASE_VERSION = 3

        // User Table
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_CREATED_AT = "created_at"

        // Categories Table
        const val TABLE_CATEGORIES = "categories"
        const val COLUMN_CATEGORY_ID = "category_id"
        const val COLUMN_CATEGORY_NAME = "category_name"
        const val COLUMN_CATEGORY_COLOR = "category_color"
        const val COLUMN_CATEGORY_ICON = "category_icon"
        const val COLUMN_CATEGORY_USER_ID = "user_id"
        const val COLUMN_CATEGORY_SHARED_BUDGET_ID = "shared_budget_id"

        // Expenses Table
        const val TABLE_EXPENSES = "expenses"
        const val COLUMN_EXPENSE_ID = "expense_id"
        const val COLUMN_EXPENSE_AMOUNT = "amount"
        const val COLUMN_EXPENSE_DATE = "date"
        const val COLUMN_EXPENSE_DESCRIPTION = "description"
        const val COLUMN_EXPENSE_CATEGORY_ID = "category_id"
        const val COLUMN_EXPENSE_PHOTO_PATH = "photo_path"
        const val COLUMN_EXPENSE_USER_ID = "user_id"
        const val COLUMN_EXPENSE_SHARED_BUDGET_ID = "shared_budget_id"

        // Budget Goals Table
        const val TABLE_BUDGET_GOALS = "budget_goals"
        const val COLUMN_BUDGET_ID = "budget_id"
        const val COLUMN_BUDGET_CATEGORY_ID = "category_id"
        const val COLUMN_BUDGET_MIN_AMOUNT = "min_amount"
        const val COLUMN_BUDGET_MAX_AMOUNT = "max_amount"
        const val COLUMN_BUDGET_MONTH = "month"
        const val COLUMN_BUDGET_YEAR = "year"
        const val COLUMN_BUDGET_USER_ID = "user_id"
        const val COLUMN_BUDGET_SHARED_BUDGET_ID = "shared_budget_id"

        // User Achievements Table
        const val TABLE_USER_ACHIEVEMENTS = "user_achievements"
        const val COLUMN_ACHIEVEMENT_ID = "achievement_id"
        const val COLUMN_ACHIEVEMENT_TYPE = "achievement_type"
        const val COLUMN_ACHIEVEMENT_USER_ID = "user_id"
        const val COLUMN_ACHIEVEMENT_DATE = "achievement_date"

        // Shared Budgets Table
        const val TABLE_SHARED_BUDGETS = "shared_budgets"
        const val COLUMN_SHARED_BUDGET_ID = "shared_budget_id"
        const val COLUMN_SHARED_BUDGET_NAME = "budget_name"
        const val COLUMN_SHARED_BUDGET_OWNER_ID = "owner_id"

        // Shared Budget Members Table
        const val TABLE_SHARED_BUDGET_MEMBERS = "shared_budget_members"
        const val COLUMN_MEMBER_ID = "member_id"
        const val COLUMN_MEMBER_SHARED_BUDGET_ID = "shared_budget_id"
        const val COLUMN_MEMBER_USER_ID = "user_id"
        const val COLUMN_MEMBER_JOINED_AT = "joined_at"

        // Loans Table
        const val TABLE_LOANS = "loans"
        const val COLUMN_LOAN_ID = "loan_id"
        const val COLUMN_LOAN_NAME = "loan_name"
        const val COLUMN_LOAN_PRINCIPAL = "principal_amount"
        const val COLUMN_LOAN_INTEREST_RATE = "interest_rate"
        const val COLUMN_LOAN_MIN_PAYMENT = "minimum_payment"
        const val COLUMN_LOAN_CURRENT_BALANCE = "current_balance"
        const val COLUMN_LOAN_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Users Table
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_CREATED_AT TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()

        // Create Categories Table
        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY_NAME TEXT NOT NULL,
                $COLUMN_CATEGORY_COLOR TEXT NOT NULL,
                $COLUMN_CATEGORY_ICON TEXT NOT NULL,
                $COLUMN_CATEGORY_USER_ID INTEGER NOT NULL,
                $COLUMN_CATEGORY_SHARED_BUDGET_ID INTEGER,
                $COLUMN_CREATED_AT TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_CATEGORY_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE,
                FOREIGN KEY($COLUMN_CATEGORY_SHARED_BUDGET_ID) REFERENCES $TABLE_SHARED_BUDGETS($COLUMN_SHARED_BUDGET_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        // Create Expenses Table
        val createExpensesTable = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EXPENSE_AMOUNT REAL NOT NULL,
                $COLUMN_EXPENSE_DATE TEXT NOT NULL,
                $COLUMN_EXPENSE_DESCRIPTION TEXT,
                $COLUMN_EXPENSE_CATEGORY_ID INTEGER NOT NULL,
                $COLUMN_EXPENSE_PHOTO_PATH TEXT,
                $COLUMN_EXPENSE_USER_ID INTEGER NOT NULL,
                $COLUMN_EXPENSE_SHARED_BUDGET_ID INTEGER,
                $COLUMN_CREATED_AT TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_EXPENSE_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CATEGORY_ID) ON DELETE CASCADE,
                FOREIGN KEY($COLUMN_EXPENSE_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE,
                FOREIGN KEY($COLUMN_EXPENSE_SHARED_BUDGET_ID) REFERENCES $TABLE_SHARED_BUDGETS($COLUMN_SHARED_BUDGET_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        // Create Budget Goals Table
        val createBudgetGoalsTable = """
            CREATE TABLE $TABLE_BUDGET_GOALS (
                $COLUMN_BUDGET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BUDGET_CATEGORY_ID INTEGER,
                $COLUMN_BUDGET_MIN_AMOUNT REAL NOT NULL,
                $COLUMN_BUDGET_MAX_AMOUNT REAL NOT NULL,
                $COLUMN_BUDGET_MONTH INTEGER NOT NULL,
                $COLUMN_BUDGET_YEAR INTEGER NOT NULL,
                $COLUMN_BUDGET_USER_ID INTEGER NOT NULL,
                $COLUMN_BUDGET_SHARED_BUDGET_ID INTEGER,
                $COLUMN_CREATED_AT TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_BUDGET_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_CATEGORY_ID) ON DELETE CASCADE,
                FOREIGN KEY($COLUMN_BUDGET_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE,
                FOREIGN KEY($COLUMN_BUDGET_SHARED_BUDGET_ID) REFERENCES $TABLE_SHARED_BUDGETS($COLUMN_SHARED_BUDGET_ID) ON DELETE CASCADE,
                UNIQUE($COLUMN_BUDGET_CATEGORY_ID, $COLUMN_BUDGET_MONTH, $COLUMN_BUDGET_YEAR, $COLUMN_BUDGET_USER_ID, $COLUMN_BUDGET_SHARED_BUDGET_ID)
            )
        """.trimIndent()

        // Create User Achievements Table
        val createUserAchievementsTable = """
            CREATE TABLE $TABLE_USER_ACHIEVEMENTS (
                $COLUMN_ACHIEVEMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACHIEVEMENT_TYPE TEXT NOT NULL,
                $COLUMN_ACHIEVEMENT_USER_ID INTEGER NOT NULL,
                $COLUMN_ACHIEVEMENT_DATE TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_ACHIEVEMENT_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE,
                UNIQUE($COLUMN_ACHIEVEMENT_TYPE, $COLUMN_ACHIEVEMENT_USER_ID)
            )
        """.trimIndent()

        // Create Shared Budgets Table
        val createSharedBudgetsTable = """
            CREATE TABLE $TABLE_SHARED_BUDGETS (
                $COLUMN_SHARED_BUDGET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SHARED_BUDGET_NAME TEXT NOT NULL,
                $COLUMN_SHARED_BUDGET_OWNER_ID INTEGER NOT NULL,
                $COLUMN_CREATED_AT TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_SHARED_BUDGET_OWNER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        // Create Shared Budget Members Table
        val createSharedBudgetMembersTable = """
            CREATE TABLE $TABLE_SHARED_BUDGET_MEMBERS (
                $COLUMN_MEMBER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MEMBER_SHARED_BUDGET_ID INTEGER NOT NULL,
                $COLUMN_MEMBER_USER_ID INTEGER NOT NULL,
                $COLUMN_MEMBER_JOINED_AT TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_MEMBER_SHARED_BUDGET_ID) REFERENCES $TABLE_SHARED_BUDGETS($COLUMN_SHARED_BUDGET_ID) ON DELETE CASCADE,
                FOREIGN KEY($COLUMN_MEMBER_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE,
                UNIQUE($COLUMN_MEMBER_SHARED_BUDGET_ID, $COLUMN_MEMBER_USER_ID)
            )
        """.trimIndent()

        // Create Loans Table
        val createLoansTable = """
            CREATE TABLE $TABLE_LOANS (
                $COLUMN_LOAN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_LOAN_NAME TEXT NOT NULL,
                $COLUMN_LOAN_PRINCIPAL REAL NOT NULL,
                $COLUMN_LOAN_INTEREST_RATE REAL NOT NULL,
                $COLUMN_LOAN_MIN_PAYMENT REAL NOT NULL,
                $COLUMN_LOAN_CURRENT_BALANCE REAL NOT NULL,
                $COLUMN_LOAN_USER_ID INTEGER NOT NULL,
                $COLUMN_CREATED_AT TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_LOAN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        db?.execSQL(createUsersTable)
        db?.execSQL(createSharedBudgetsTable)
        db?.execSQL(createSharedBudgetMembersTable)
        db?.execSQL(createCategoriesTable)
        db?.execSQL(createExpensesTable)
        db?.execSQL(createBudgetGoalsTable)
        db?.execSQL(createUserAchievementsTable)
        db?.execSQL(createLoansTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Add shared budget tables
            val createSharedBudgetsTable = """
                CREATE TABLE IF NOT EXISTS $TABLE_SHARED_BUDGETS (
                    $COLUMN_SHARED_BUDGET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_SHARED_BUDGET_NAME TEXT NOT NULL,
                    $COLUMN_SHARED_BUDGET_OWNER_ID INTEGER NOT NULL,
                    $COLUMN_CREATED_AT TEXT DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY($COLUMN_SHARED_BUDGET_OWNER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE
                )
            """.trimIndent()

            val createSharedBudgetMembersTable = """
                CREATE TABLE IF NOT EXISTS $TABLE_SHARED_BUDGET_MEMBERS (
                    $COLUMN_MEMBER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_MEMBER_SHARED_BUDGET_ID INTEGER NOT NULL,
                    $COLUMN_MEMBER_USER_ID INTEGER NOT NULL,
                    $COLUMN_MEMBER_JOINED_AT TEXT DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY($COLUMN_MEMBER_SHARED_BUDGET_ID) REFERENCES $TABLE_SHARED_BUDGETS($COLUMN_SHARED_BUDGET_ID) ON DELETE CASCADE,
                    FOREIGN KEY($COLUMN_MEMBER_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE,
                    UNIQUE($COLUMN_MEMBER_SHARED_BUDGET_ID, $COLUMN_MEMBER_USER_ID)
                )
            """.trimIndent()

            db?.execSQL(createSharedBudgetsTable)
            db?.execSQL(createSharedBudgetMembersTable)

            // Add shared_budget_id columns to existing tables
            db?.execSQL("ALTER TABLE $TABLE_CATEGORIES ADD COLUMN $COLUMN_CATEGORY_SHARED_BUDGET_ID INTEGER")
            db?.execSQL("ALTER TABLE $TABLE_EXPENSES ADD COLUMN $COLUMN_EXPENSE_SHARED_BUDGET_ID INTEGER")
            db?.execSQL("ALTER TABLE $TABLE_BUDGET_GOALS ADD COLUMN $COLUMN_BUDGET_SHARED_BUDGET_ID INTEGER")
        }

        if (oldVersion < 3) {
            // Add loans table
            val createLoansTable = """
                CREATE TABLE IF NOT EXISTS $TABLE_LOANS (
                    $COLUMN_LOAN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_LOAN_NAME TEXT NOT NULL,
                    $COLUMN_LOAN_PRINCIPAL REAL NOT NULL,
                    $COLUMN_LOAN_INTEREST_RATE REAL NOT NULL,
                    $COLUMN_LOAN_MIN_PAYMENT REAL NOT NULL,
                    $COLUMN_LOAN_CURRENT_BALANCE REAL NOT NULL,
                    $COLUMN_LOAN_USER_ID INTEGER NOT NULL,
                    $COLUMN_CREATED_AT TEXT DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY($COLUMN_LOAN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE
                )
            """.trimIndent()

            db?.execSQL(createLoansTable)
        }
    }
}
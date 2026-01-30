# PocketPlan 💰

**Smart Budgeting, Made Simple**

A comprehensive Android budget tracking application that helps users manage their finances, track expenses, set budget goals, collaborate with others, and plan debt payoff strategies.

---

## 📱 About PocketPlan

PocketPlan is a feature-rich personal finance management app designed to give users complete control over their spending. With an intuitive interface and powerful features, PocketPlan makes budgeting accessible and actionable for everyone.

### Key Highlights
- 🎯 **Zero-Based Budgeting**: Every rand has a purpose
- 📊 **Visual Analytics**: Beautiful charts and graphs
- 👥 **Collaborative Budgeting**: Share budgets with family/roommates
- 💳 **Debt Management**: Plan your path to becoming debt-free
- 🏆 **Gamification**: Earn achievements for good financial habits
- 📸 **Receipt Tracking**: Attach photos to expenses
- 🔒 **Secure & Private**: Local SQLite database

---

## ✨ Features

### Core Features

#### 1. User Authentication
- Secure user registration and login
- Password encryption (SHA-256)
- Session management
- Multi-user support

#### 2. Category Management ("Pockets")
- Create custom expense categories
- Color-coded organization
- Icon-based visual identification
- 8+ pre-defined categories:
  - 🛒 Groceries
  - 🎬 Entertainment
  - 🚗 Transport
  - 💊 Health
  - 📚 Education
  - 🛍️ Shopping
  - 💡 Bills
  - 💰 Savings

#### 3. Expense Tracking
- Quick expense entry with details:
  - Amount (South African Rand)
  - Date selection
  - Description
  - Category assignment
  - Optional photo attachment
- View expense history
- Filter by date range
- Delete/manage expenses
- Receipt photo storage and viewing

#### 4. Budget Goals
- Set minimum and maximum spending limits
- Monthly budget planning
- Overall budget goals
- Category-specific budgets
- Visual progress tracking

#### 5. Dashboard & Analytics
- **Budget Compliance Dashboard**:
  - Overall compliance score
  - Category status breakdown (On Track, Under Min, Over Budget)
  - Pie chart visualization
  - Color-coded warnings
  - Total budget vs. actual spending

- **Analytics Screen**:
  - Daily spending trends (line chart)
  - Spending breakdown (bar chart)
  - Category vs. budget goals comparison
  - Total spent, daily average, highest day metrics
  - User-selectable date ranges

#### 6. Category Spending Analysis
- Total spent per category
- Percentage breakdown
- Progress bars with visual indicators
- Date range filtering
- Export-ready data views

#### 7. Gamification & Achievements
- Unlock achievements for:
  - ✅ First expense logged
  - 🔥 7-day expense streak
  - 📅 30-day expense streak
  - 🎯 First budget goal set
  - 💪 Budget goal met
  - 🏆 3 consecutive budget goals met
  - 💚 Staying under budget
  - 👑 Category master (5+ expenses in one category)
  - 💯 100 total expenses
  - 💰 Saver (spending below minimum)
  - 📸 10 receipts uploaded

### Custom Features

#### 8. Shared Budgeting 👥
*Collaborate on budgets with family, friends, or roommates*

- **Create Shared Budget Groups**:
  - Name your shared budget (e.g., "Family Budget", "Roommate Expenses")
  - Automatic owner assignment
  - Invite members by username

- **Member Management**:
  - View all budget members
  - Add/remove members (owner only)
  - Role-based permissions (Owner vs. Member)
  - Member join date tracking

- **Collaborative Features**:
  - All members see shared expenses
  - All members can add expenses
  - Shared categories and budget goals
  - Real-time synchronization
  - Multiple shared budgets per user

- **Security & Privacy**:
  - Owner-only budget deletion
  - Personal data remains private
  - Username-based invitations
  - Member-specific permissions

#### 9. Loan Planner 💳
*Plan your path to becoming debt-free*

- **Loan Management**:
  - Add multiple loans (student, car, credit card, personal)
  - Track key metrics:
    - Original principal amount
    - Current balance
    - Annual interest rate (APR)
    - Minimum monthly payment
  - Visual loan cards with status indicators
  - Total debt calculation across all loans

- **Automatic Calculations**:
  - Months to payoff
  - Debt-free date (exact month/year)
  - Total interest paid
  - Total amount paid (principal + interest)
  - Low payment warnings

- **Payment Scenario Comparison**:
  - **Minimum Payment**: Baseline scenario
  - **Minimum + R100**: Small extra payment
  - **Minimum + R250**: Moderate extra payment
  - **Minimum + R500**: Significant extra payment
  - **Double Payment**: Aggressive payoff strategy

- **Visual Analytics**:
  - Bar chart comparing all scenarios
  - Color-coded efficiency (Red = slowest, Green = fastest)
  - Interest savings calculator
  - Time savings visualization

- **Financial Insights**:
  - See true cost of debt (total interest)
  - Compare payment strategies side-by-side
  - Calculate interest savings from extra payments
  - Make informed debt payoff decisions

---

## 🛠️ Technical Stack

### Platform & Language
- **Platform**: Android (SDK 24+)
- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

### Architecture & Design
- **Architecture**: Activity-based with DAO pattern
- **UI Design**: Material Design Components
- **Layout**: LinearLayout, RecyclerView, CardView
- **Navigation**: Intent-based navigation

### Database
- **Database**: SQLite (Local storage)
- **ORM**: Custom DAO implementation
- **Tables**: 8 tables
  - users
  - categories
  - expenses
  - budget_goals
  - user_achievements
  - shared_budgets
  - shared_budget_members
  - loans

### Libraries & Dependencies
```gradle
// Material Design
implementation 'com.google.android.material:material:1.11.0'

// Charts & Graphs
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

// UI Components
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
```

### Security
- **Password Hashing**: SHA-256
- **Session Management**: Persistent login sessions
- **Data Privacy**: Local-only storage (no cloud)
- **User Isolation**: Foreign key constraints

---

## 📊 Database Schema

### Tables Overview

#### users
```sql
user_id (PK) | username (UNIQUE) | password | created_at
```

#### categories
```sql
category_id (PK) | category_name | category_color | category_icon | 
user_id (FK) | shared_budget_id (FK, nullable) | created_at
```

#### expenses
```sql
expense_id (PK) | amount | date | description | category_id (FK) | 
photo_path | user_id (FK) | shared_budget_id (FK, nullable) | created_at
```

#### budget_goals
```sql
budget_id (PK) | category_id (FK, nullable) | min_amount | max_amount | 
month | year | user_id (FK) | shared_budget_id (FK, nullable) | created_at
UNIQUE(category_id, month, year, user_id, shared_budget_id)
```

#### user_achievements
```sql
achievement_id (PK) | achievement_type | user_id (FK) | achievement_date
UNIQUE(achievement_type, user_id)
```

#### shared_budgets
```sql
shared_budget_id (PK) | budget_name | owner_id (FK) | created_at
```

#### shared_budget_members
```sql
member_id (PK) | shared_budget_id (FK) | user_id (FK) | joined_at
UNIQUE(shared_budget_id, user_id)
```

#### loans
```sql
loan_id (PK) | loan_name | principal_amount | interest_rate | 
minimum_payment | current_balance | user_id (FK) | created_at
```

---

## 🎨 User Interface

### Design Principles
- **Color Scheme**: Purple primary (#8B7BA8), with accent colors
- **Typography**: Material Design standard fonts
- **Icons**: Emoji-based for categories (🛒, 🎬, 🚗, etc.)
- **Cards**: Rounded corners (12dp radius), elevated shadows
- **Spacing**: Consistent 16dp padding/margins

### Color Palette
```xml
Primary: #8B7BA8 (Purple)
Accent: #9C88B5 (Light Purple)
Background: #F5F3F7 (Light Gray)
Success: #4CAF50 (Green)
Warning: #FFC107 (Yellow)
Error: #F44336 (Red)
```

### Theme
- Material Components theme
- Light theme with purple accents
- Consistent button styling
- Color-coded status indicators

---

## 📸 Screenshots

### Main Features
- Login & Registration screens
- Main Dashboard with categorized menu
- Category management with color-coded pockets
- Expense entry with photo attachment
- Budget goals setup (overall & category-specific)
- Expense list with filtering
- Receipt photo viewer

### Analytics
- Budget compliance dashboard with pie chart
- Category spending vs. budget goals (grouped bar chart)
- Daily spending trends (line chart)
- Category spending breakdown

### Custom Features
- Shared budget list and details
- Member management screen
- Loan list with total debt
- Loan details with payment scenarios
- Payment comparison chart

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK 24 or higher
- Kotlin plugin
- Gradle 7.0+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/pocketplan.git
cd pocketplan
```

2. **Open in Android Studio**
- File → Open → Select project directory
- Wait for Gradle sync to complete

3. **Build the project**
- Build → Clean Project
- Build → Rebuild Project

4. **Run the app**
- Select emulator or physical device
- Run → Run 'app'

### First Run
1. Launch app (shows splash screen)
2. Register a new account
3. Login with credentials
4. Explore features from main menu

---

## 📖 Usage Guide

### Quick Start

#### Setting Up Your Budget
1. **Create Categories**: Tap "📁 Categories" → Add your spending categories
2. **Set Budget Goals**: Tap "🎯 Budget Goals" → Set min/max spending limits
3. **Add Expenses**: Tap "➕ Add Expense" → Record your spending
4. **Monitor Progress**: Tap "📊 Dashboard" → See budget compliance

#### Using Shared Budgets
1. Tap "👥 Shared Budgets"
2. Tap (+) FAB to create a shared budget
3. Enter budget name (e.g., "Family Budget")
4. Tap the budget → "Invite Member"
5. Enter friend's username → They can now collaborate!

#### Planning Debt Payoff
1. Tap "💳 Loan Planner"
2. Tap (+) FAB to add a loan
3. Enter loan details (balance, interest rate, payment)
4. Tap loan to see payoff scenarios
5. Compare payment options and savings

### Tips for Success
- 📸 **Attach receipts**: Take photos of receipts for better tracking
- 📅 **Log daily**: Add expenses every day for accurate data
- 🎯 **Set realistic goals**: Start with achievable min/max budgets
- 📊 **Review weekly**: Check your dashboard weekly to stay on track
- 🏆 **Chase achievements**: Use gamification to build good habits
- 👥 **Share with family**: Collaborate on household budgets
- 💳 **Plan debt**: Use loan planner to save on interest

---

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/pocketplan/
│   │   ├── database/
│   │   │   ├── DatabaseHelper.kt
│   │   │   ├── CategoryDao.kt
│   │   │   ├── ExpenseDao.kt
│   │   │   ├── BudgetGoalDao.kt
│   │   │   ├── AchievementDao.kt
│   │   │   ├── SharedBudgetDao.kt
│   │   │   └── LoanDao.kt
│   │   ├── models/
│   │   │   ├── User.kt
│   │   │   ├── Category.kt
│   │   │   ├── Expense.kt
│   │   │   ├── BudgetGoal.kt
│   │   │   ├── BudgetProgress.kt
│   │   │   ├── Achievement.kt
│   │   │   ├── SharedBudget.kt
│   │   │   ├── SharedBudgetMember.kt
│   │   │   ├── Loan.kt
│   │   │   ├── PaymentScenario.kt
│   │   │   └── PaymentBreakdown.kt
│   │   ├── ui/
│   │   │   ├── SplashActivity.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── auth/
│   │   │   │   ├── LoginActivity.kt
│   │   │   │   └── RegisterActivity.kt
│   │   │   ├── categories/
│   │   │   │   ├── CategoriesActivity.kt
│   │   │   │   ├── CategoryAdapter.kt
│   │   │   │   └── AddCategoryDialog.kt
│   │   │   ├── expenses/
│   │   │   │   ├── AddExpenseActivity.kt
│   │   │   │   ├── ExpensesListActivity.kt
│   │   │   │   ├── ExpenseAdapter.kt
│   │   │   │   └── ViewReceiptDialog.kt
│   │   │   ├── budget/
│   │   │   │   ├── BudgetGoalsActivity.kt
│   │   │   │   ├── BudgetGoalAdapter.kt
│   │   │   │   ├── SetOverallBudgetDialog.kt
│   │   │   │   └── SetCategoryBudgetDialog.kt
│   │   │   ├── dashboard/
│   │   │   │   ├── DashboardActivity.kt
│   │   │   │   └── BudgetProgressAdapter.kt
│   │   │   ├── analytics/
│   │   │   │   ├── AnalyticsActivity.kt
│   │   │   │   ├── CategorySpendingActivity.kt
│   │   │   │   └── CategorySpendingAdapter.kt
│   │   │   ├── achievements/
│   │   │   │   ├── AchievementsActivity.kt
│   │   │   │   └── AchievementAdapter.kt
│   │   │   ├── shared/
│   │   │   │   ├── SharedBudgetActivity.kt
│   │   │   │   ├── SharedBudgetDetailsActivity.kt
│   │   │   │   ├── SharedBudgetAdapter.kt
│   │   │   │   ├── SharedBudgetMemberAdapter.kt
│   │   │   │   ├── CreateSharedBudgetDialog.kt
│   │   │   │   └── InviteMemberDialog.kt
│   │   │   └── loans/
│   │   │       ├── LoanPlannerActivity.kt
│   │   │       ├── LoanDetailsActivity.kt
│   │   │       ├── LoanAdapter.kt
│   │   │       ├── PaymentScenarioAdapter.kt
│   │   │       └── AddLoanDialog.kt
│   │   └── utils/
│   │       ├── SessionManager.kt
│   │       ├── ValidationUtils.kt
│   │       ├── PasswordUtils.kt
│   │       └── LoanCalculator.kt
│   └── res/
│       ├── layout/ (35+ XML layout files)
│       ├── drawable/ (20+ icons and backgrounds)
│       ├── values/
│       │   ├── colors.xml
│       │   ├── strings.xml
│       │   └── themes.xml
│       └── mipmap/ (App icons)
└── build.gradle
```

---

## 🧪 Testing

### Manual Testing Checklist

#### Authentication
- ✅ User can register with username and password
- ✅ Passwords are validated (minimum length, etc.)
- ✅ User can login with correct credentials
- ✅ Login fails with incorrect credentials
- ✅ Session persists after app restart
- ✅ Logout clears session

#### Categories
- ✅ User can create new category with name, color, icon
- ✅ Categories appear in list
- ✅ User can delete category
- ✅ Deleting category doesn't break expenses

#### Expenses
- ✅ User can add expense with all fields
- ✅ Photo attachment works
- ✅ Expenses appear in list
- ✅ Date filtering works
- ✅ Receipt photos can be viewed
- ✅ User can delete expense

#### Budget Goals
- ✅ User can set overall budget
- ✅ User can set category budgets
- ✅ Goals appear in list
- ✅ User can delete goals

#### Dashboard
- ✅ Compliance score calculates correctly
- ✅ Pie chart shows correct distribution
- ✅ Color coding works (red, green, blue)
- ✅ Status cards show correct counts
- ✅ Warnings appear when over budget

#### Analytics
- ✅ Charts display spending data
- ✅ Category vs. budget chart works
- ✅ Date filtering works
- ✅ Statistics calculate correctly

#### Shared Budgets
- ✅ User can create shared budget
- ✅ User can invite members
- ✅ Members see shared budget
- ✅ Owner can remove members
- ✅ Owner can delete budget

#### Loan Planner
- ✅ User can add loan
- ✅ Calculations are accurate
- ✅ Payment scenarios generate
- ✅ Chart displays correctly
- ✅ Low payment warning works

---

## 🤝 Contributing

This is a student project for academic purposes. However, feedback and suggestions are welcome!

### How to Contribute
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 Documentation

### Additional Documentation
- **COMPLETE_SHARED_BUDGETING_GUIDE.md** - Detailed guide for Shared Budgeting feature
- **COMPLETE_LOAN_PLANNER_GUIDE.md** - Detailed guide for Loan Planner feature
- **INTEGRATION_GUIDE.md** - Instructions for connecting custom features

### Code Documentation
- All classes include KDoc comments
- Complex algorithms explained inline
- Database schema documented in DatabaseHelper

---

## 🐛 Known Issues

### Current Limitations
- **Offline Only**: No cloud sync (by design for privacy)
- **Single Currency**: Only supports South African Rand (R)
- **No Export**: Cannot export data to CSV/Excel (future enhancement)
- **No Recurring Expenses**: Manual entry required for recurring bills
- **No Notifications**: No payment reminders (future enhancement)

### Workarounds
- **Multiple Devices**: Use shared budgets to collaborate across devices
- **Data Backup**: Manually backup database file from device storage
- **Currency**: Use conversion rate manually for other currencies

---

## 🔮 Future Enhancements

### Planned Features
- [ ] Cloud backup and sync
- [ ] Recurring expense automation
- [ ] Payment reminders/notifications
- [ ] Data export to CSV/Excel
- [ ] Multi-currency support
- [ ] Bill splitting calculator
- [ ] Receipt OCR (text extraction)
- [ ] Spending predictions using ML
- [ ] Widget for home screen
- [ ] Dark mode theme
- [ ] Biometric authentication
- [ ] Categories import/export
- [ ] Debt snowball/avalanche recommendations

---

## 📜 License

This project is developed as part of an academic course requirement.

**Copyright © 2026 Tshepo Motloung**

All rights reserved. This project is for educational purposes only.

---

## 👤 Author

**Tshepo Motloung**

- **Course**: OPSC6311 - Mobile Application Development
- **Institution**: The Independent Institute of Education (IIE)
- **Year**: 2026
- **Project**: PocketPlan - Personal Budget Tracker

---

## 🙏 Acknowledgments

### Inspiration
- **YNAB (You Need A Budget)** - Zero-based budgeting philosophy
- **Goodbudget** - Envelope budgeting system
- **EveryDollar** - Simplicity and user-friendliness

### Resources
- **Material Design Guidelines** - UI/UX design principles
- **Android Developer Documentation** - Technical implementation
- **MPAndroidChart Library** - Beautiful charts and graphs
- **Stack Overflow Community** - Problem-solving assistance

### Special Thanks
- Course instructors for guidance and feedback
- Classmates for testing and suggestions
- Family for inspiration to create better financial tools

---

## 📞 Support

For questions, issues, or feedback:
- **GitHub Issues**: [Create an issue](https://github.com/yourusername/pocketplan/issues)
- **Email**: sktshepo735@gamail.com
- **Course Forum**: OPSC6311 discussion board

---

## 🎉 Project Status

**Status**: ✅ Complete and Ready for Submission

**Version**: 1.0.0

**Last Updated**: January 2026

**Submission Date**: [Your Submission Date]

---

## 📊 Project Statistics

- **Total Files**: 100+ files
- **Lines of Code**: ~15,000 lines
- **Development Time**: [X weeks]
- **Features Implemented**: 13 major features
- **Database Tables**: 8 tables
- **Activities**: 20+ screens
- **Custom Features**: 2 (Shared Budgeting, Loan Planner)

---

**Made with ❤️ and Kotlin**

*"Smart Budgeting, Made Simple"* - PocketPlan 💰

package com.pocketplan.ui.analytics

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.example.pocketplan.R
import com.pocketplan.database.ExpenseDao
import com.pocketplan.utils.SessionManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var tvDateRange: TextView
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvAverageDaily: TextView
    private lateinit var tvHighestDay: TextView
    private lateinit var btnFilterStart: Button
    private lateinit var btnFilterEnd: Button
    private lateinit var btnCurrentMonth: Button
    private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart

    private lateinit var expenseDao: ExpenseDao
    private lateinit var sessionManager: SessionManager

    private var startDate: Calendar = Calendar.getInstance()
    private var endDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        supportActionBar?.title = "Spending Analytics"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        expenseDao = ExpenseDao(this)
        sessionManager = SessionManager(this)

        // Set default to current month
        setCurrentMonth()

        setupCharts()
        loadAnalytics()

        btnFilterStart.setOnClickListener {
            showStartDatePicker()
        }

        btnFilterEnd.setOnClickListener {
            showEndDatePicker()
        }

        btnCurrentMonth.setOnClickListener {
            setCurrentMonth()
            loadAnalytics()
        }
    }

    private fun initViews() {
        tvDateRange = findViewById(R.id.tvDateRange)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        tvAverageDaily = findViewById(R.id.tvAverageDaily)
        tvHighestDay = findViewById(R.id.tvHighestDay)
        btnFilterStart = findViewById(R.id.btnFilterStart)
        btnFilterEnd = findViewById(R.id.btnFilterEnd)
        btnCurrentMonth = findViewById(R.id.btnCurrentMonth)
        lineChart = findViewById(R.id.lineChart)
        barChart = findViewById(R.id.barChart)
    }

    private fun setCurrentMonth() {
        startDate = Calendar.getInstance()
        startDate.set(Calendar.DAY_OF_MONTH, 1)

        endDate = Calendar.getInstance()
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH))

        updateDateRangeDisplay()
    }

    private fun setupCharts() {
        // Line Chart Setup
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(false)
            legend.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.textColor = Color.parseColor("#757575")
            axisLeft.textColor = Color.parseColor("#757575")
            axisLeft.setDrawGridLines(true)
            axisLeft.gridColor = Color.parseColor("#E0E0E0")
            axisRight.isEnabled = false
        }

        // Bar Chart Setup
        barChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(false)
            legend.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.textColor = Color.parseColor("#757575")
            axisLeft.textColor = Color.parseColor("#757575")
            axisLeft.setDrawGridLines(true)
            axisLeft.gridColor = Color.parseColor("#E0E0E0")
            axisRight.isEnabled = false
        }
    }

    private fun updateDateRangeDisplay() {
        val start = displayDateFormat.format(startDate.time)
        val end = displayDateFormat.format(endDate.time)
        tvDateRange.text = "$start - $end"
    }

    private fun loadAnalytics() {
        val userId = sessionManager.getUserId()
        val startDateStr = dateFormat.format(startDate.time)
        val endDateStr = dateFormat.format(endDate.time)

        val dailySpending = expenseDao.getDailySpending(userId, startDateStr, endDateStr)

        if (dailySpending.isEmpty()) {
            tvTotalSpent.text = "Total: R 0.00"
            tvAverageDaily.text = "Daily Avg: R 0.00"
            tvHighestDay.text = "Highest: R 0.00"
            lineChart.visibility = View.GONE
            barChart.visibility = View.GONE
            return
        }

        lineChart.visibility = View.VISIBLE
        barChart.visibility = View.VISIBLE

        // Calculate statistics
        val total = dailySpending.sumOf { it.totalAmount }
        val dayCount = dailySpending.size
        val average = total / dayCount
        val highest = dailySpending.maxOf { it.totalAmount }

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        tvTotalSpent.text = "Total: ${currencyFormat.format(total)}"
        tvAverageDaily.text = "Daily Avg: ${currencyFormat.format(average)}"
        tvHighestDay.text = "Highest: ${currencyFormat.format(highest)}"

        // Update charts
        updateLineChart(dailySpending)
        updateBarChart(dailySpending)
    }

    private fun updateLineChart(dailySpending: List<com.pocketplan.models.DailySpending>) {
        val entries = dailySpending.mapIndexed { index, spending ->
            Entry(index.toFloat(), spending.totalAmount.toFloat())
        }

        val dataSet = LineDataSet(entries, "Daily Spending").apply {
            color = Color.parseColor("#8B7BA8")
            setCircleColor(Color.parseColor("#8B7BA8"))
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            fillColor = Color.parseColor("#8B7BA8")
            fillAlpha = 50
            setDrawFilled(true)
        }

        lineChart.data = LineData(dataSet)
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                if (index >= 0 && index < dailySpending.size) {
                    val date = dailySpending[index].date
                    return try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("dd", Locale.getDefault())
                        val parsedDate = inputFormat.parse(date)
                        parsedDate?.let { outputFormat.format(it) } ?: ""
                    } catch (e: Exception) {
                        ""
                    }
                }
                return ""
            }
        }
        lineChart.invalidate()
    }

    private fun updateBarChart(dailySpending: List<com.pocketplan.models.DailySpending>) {
        val entries = dailySpending.mapIndexed { index, spending ->
            BarEntry(index.toFloat(), spending.totalAmount.toFloat())
        }

        val dataSet = BarDataSet(entries, "Daily Spending").apply {
            color = Color.parseColor("#8B7BA8")
            setDrawValues(false)
        }

        barChart.data = BarData(dataSet)
        barChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                if (index >= 0 && index < dailySpending.size) {
                    val date = dailySpending[index].date
                    return try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("dd", Locale.getDefault())
                        val parsedDate = inputFormat.parse(date)
                        parsedDate?.let { outputFormat.format(it) } ?: ""
                    } catch (e: Exception) {
                        ""
                    }
                }
                return ""
            }
        }
        barChart.invalidate()
    }

    private fun showStartDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                startDate.set(year, month, dayOfMonth)
                updateDateRangeDisplay()
                loadAnalytics()
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
                loadAnalytics()
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
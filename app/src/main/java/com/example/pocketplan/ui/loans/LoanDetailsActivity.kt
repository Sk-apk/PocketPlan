package com.pocketplan.ui.loans

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.example.pocketplan.R
import com.pocketplan.database.LoanDao
import com.pocketplan.models.Loan
import com.pocketplan.utils.LoanCalculator
import java.text.NumberFormat
import java.util.*

class LoanDetailsActivity : AppCompatActivity() {

    private lateinit var tvLoanName: TextView
    private lateinit var tvCurrentBalance: TextView
    private lateinit var tvInterestRate: TextView
    private lateinit var tvMinPayment: TextView
    private lateinit var tvPayoffDate: TextView
    private lateinit var tvTotalInterest: TextView
    private lateinit var barChart: BarChart
    private lateinit var rvScenarios: RecyclerView

    private lateinit var loanDao: LoanDao
    private lateinit var scenarioAdapter: PaymentScenarioAdapter

    private var loanId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_details)

        supportActionBar?.title = "Loan Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loanId = intent.getIntExtra("LOAN_ID", 0)

        initViews()
        loanDao = LoanDao(this)

        setupChart()
        loadLoanDetails()
    }

    private fun initViews() {
        tvLoanName = findViewById(R.id.tvLoanName)
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance)
        tvInterestRate = findViewById(R.id.tvInterestRate)
        tvMinPayment = findViewById(R.id.tvMinPayment)
        tvPayoffDate = findViewById(R.id.tvPayoffDate)
        tvTotalInterest = findViewById(R.id.tvTotalInterest)
        barChart = findViewById(R.id.barChart)
        rvScenarios = findViewById(R.id.rvScenarios)
    }

    private fun setupChart() {
        barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            legend.isEnabled = true
            legend.textColor = Color.parseColor("#757575")
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.textColor = Color.parseColor("#757575")
            axisLeft.textColor = Color.parseColor("#757575")
            axisLeft.setDrawGridLines(true)
            axisLeft.gridColor = Color.parseColor("#E0E0E0")
            axisRight.isEnabled = false
        }
    }

    private fun loadLoanDetails() {
        val loan = loanDao.getLoanById(loanId) ?: return
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

        tvLoanName.text = loan.loanName
        tvCurrentBalance.text = currencyFormat.format(loan.currentBalance)
        tvInterestRate.text = "${String.format("%.2f", loan.interestRate)}% APR"
        tvMinPayment.text = currencyFormat.format(loan.minimumPayment)

        // Calculate payoff date
        val payoffDate = LoanCalculator.calculatePayoffDate(
            loan.currentBalance,
            loan.interestRate,
            loan.minimumPayment
        )
        tvPayoffDate.text = payoffDate

        // Calculate total interest
        val totalInterest = LoanCalculator.calculateTotalInterest(
            loan.currentBalance,
            loan.interestRate,
            loan.minimumPayment
        )
        tvTotalInterest.text = if (totalInterest == Double.MAX_VALUE) {
            "∞ (payment too low)"
        } else {
            currencyFormat.format(totalInterest)
        }

        // Generate payment scenarios
        val scenarios = LoanCalculator.generatePaymentScenarios(loan)

        scenarioAdapter = PaymentScenarioAdapter(scenarios)
        rvScenarios.apply {
            layoutManager = LinearLayoutManager(this@LoanDetailsActivity)
            adapter = scenarioAdapter
        }

        // Update chart
        updateChart(scenarios)
    }

    private fun updateChart(scenarios: List<com.pocketplan.models.PaymentScenario>) {
        val entries = scenarios.mapIndexed { index, scenario ->
            BarEntry(index.toFloat(), scenario.monthsToPayoff.toFloat())
        }

        val dataSet = BarDataSet(entries, "Months to Payoff").apply {
            colors = listOf(
                Color.parseColor("#F44336"), // Red - Minimum
                Color.parseColor("#FF9800"), // Orange
                Color.parseColor("#FFC107"), // Yellow
                Color.parseColor("#8BC34A"), // Light green
                Color.parseColor("#4CAF50")  // Green - Best
            )
            valueTextSize = 11f
            valueTextColor = Color.parseColor("#757575")
        }

        barChart.data = BarData(dataSet)
        barChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < scenarios.size) {
                    scenarios[index].scenarioName
                } else {
                    ""
                }
            }
        }
        barChart.xAxis.labelRotationAngle = -45f
        barChart.invalidate()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
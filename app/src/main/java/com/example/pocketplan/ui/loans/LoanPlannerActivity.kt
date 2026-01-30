package com.pocketplan.ui.loans

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.pocketplan.R
import com.pocketplan.database.LoanDao
import com.pocketplan.models.Loan
import com.pocketplan.utils.LoanCalculator
import com.pocketplan.utils.SessionManager
import java.text.NumberFormat
import java.util.*

class LoanPlannerActivity : AppCompatActivity() {

    private lateinit var tvTotalDebt: TextView
    private lateinit var tvNoLoans: TextView
    private lateinit var rvLoans: RecyclerView
    private lateinit var fabAddLoan: FloatingActionButton

    private lateinit var loanDao: LoanDao
    private lateinit var sessionManager: SessionManager
    private lateinit var loanAdapter: LoanAdapter

    private var loans = mutableListOf<Loan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan_planner)

        supportActionBar?.title = "Loan Planner"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        loanDao = LoanDao(this)
        sessionManager = SessionManager(this)

        setupRecyclerView()
        loadLoans()

        fabAddLoan.setOnClickListener {
            showAddLoanDialog()
        }
    }

    private fun initViews() {
        tvTotalDebt = findViewById(R.id.tvTotalDebt)
        tvNoLoans = findViewById(R.id.tvNoLoans)
        rvLoans = findViewById(R.id.rvLoans)
        fabAddLoan = findViewById(R.id.fabAddLoan)
    }

    private fun setupRecyclerView() {
        loanAdapter = LoanAdapter(
            loans = loans,
            onItemClick = { loan ->
                openLoanDetails(loan)
            },
            onDeleteClick = { loan ->
                deleteLoan(loan)
            }
        )

        rvLoans.apply {
            layoutManager = LinearLayoutManager(this@LoanPlannerActivity)
            adapter = loanAdapter
        }
    }

    private fun loadLoans() {
        val userId = sessionManager.getUserId()
        loans.clear()
        loans.addAll(loanDao.getAllLoans(userId))

        // Calculate payoff details for each loan
        loans.forEach { loan ->
            loan.monthsToPayoff = LoanCalculator.calculateMonthsToPayoff(
                loan.currentBalance,
                loan.interestRate,
                loan.minimumPayment
            )
            loan.totalInterestPaid = LoanCalculator.calculateTotalInterest(
                loan.currentBalance,
                loan.interestRate,
                loan.minimumPayment
            )
            loan.totalAmountPaid = loan.currentBalance + loan.totalInterestPaid
        }

        loanAdapter.notifyDataSetChanged()

        // Update total debt
        val totalDebt = loanDao.getTotalDebt(userId)
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        tvTotalDebt.text = currencyFormat.format(totalDebt)

        // Show/hide empty state
        if (loans.isEmpty()) {
            tvNoLoans.visibility = View.VISIBLE
            rvLoans.visibility = View.GONE
        } else {
            tvNoLoans.visibility = View.GONE
            rvLoans.visibility = View.VISIBLE
        }
    }

    private fun showAddLoanDialog() {
        val dialog = AddLoanDialog { loanName, principal, interestRate, minPayment, currentBalance ->
            addLoan(loanName, principal, interestRate, minPayment, currentBalance)
        }
        dialog.show(supportFragmentManager, "AddLoanDialog")
    }

    private fun addLoan(
        loanName: String,
        principal: Double,
        interestRate: Double,
        minPayment: Double,
        currentBalance: Double
    ) {
        val userId = sessionManager.getUserId()

        val loan = Loan(
            loanName = loanName,
            principalAmount = principal,
            interestRate = interestRate,
            minimumPayment = minPayment,
            currentBalance = currentBalance,
            userId = userId
        )

        val loanId = loanDao.addLoan(loan)

        if (loanId != -1L) {
            Toast.makeText(this, "Loan added successfully!", Toast.LENGTH_SHORT).show()
            loadLoans()
        } else {
            Toast.makeText(this, "Failed to add loan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openLoanDetails(loan: Loan) {
        val intent = Intent(this, LoanDetailsActivity::class.java)
        intent.putExtra("LOAN_ID", loan.loanId)
        startActivity(intent)
    }

    private fun deleteLoan(loan: Loan) {
        AlertDialog.Builder(this)
            .setTitle("Delete Loan")
            .setMessage("Are you sure you want to delete '${loan.loanName}'?")
            .setPositiveButton("Delete") { _, _ ->
                val success = loanDao.deleteLoan(loan.loanId)
                if (success) {
                    Toast.makeText(this, "Loan deleted", Toast.LENGTH_SHORT).show()
                    loadLoans()
                } else {
                    Toast.makeText(this, "Failed to delete loan", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        loadLoans()
    }
}
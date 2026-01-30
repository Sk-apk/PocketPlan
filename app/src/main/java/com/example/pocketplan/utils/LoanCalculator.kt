package com.pocketplan.utils

import com.pocketplan.models.Loan
import com.pocketplan.models.PaymentBreakdown
import com.pocketplan.models.PaymentScenario
import kotlin.math.pow
import kotlin.math.ceil

object LoanCalculator {

    /**
     * Calculate months to pay off loan with given monthly payment
     */
    fun calculateMonthsToPayoff(
        balance: Double,
        annualInterestRate: Double,
        monthlyPayment: Double
    ): Int {
        if (annualInterestRate == 0.0) {
            return ceil(balance / monthlyPayment).toInt()
        }

        val monthlyRate = annualInterestRate / 100 / 12

        // If payment is less than or equal to monthly interest, loan will never be paid off
        val monthlyInterest = balance * monthlyRate
        if (monthlyPayment <= monthlyInterest) {
            return Int.MAX_VALUE // Represents "never"
        }

        // Formula: n = -log(1 - (r * P / M)) / log(1 + r)
        // where P = principal, r = monthly rate, M = monthly payment
        val numerator = kotlin.math.ln(1 - (monthlyRate * balance / monthlyPayment))
        val denominator = kotlin.math.ln(1 + monthlyRate)

        return ceil(-numerator / denominator).toInt()
    }

    /**
     * Calculate total interest paid over the life of the loan
     */
    fun calculateTotalInterest(
        balance: Double,
        annualInterestRate: Double,
        monthlyPayment: Double
    ): Double {
        val months = calculateMonthsToPayoff(balance, annualInterestRate, monthlyPayment)
        if (months == Int.MAX_VALUE) return Double.MAX_VALUE

        val totalPaid = monthlyPayment * months
        return totalPaid - balance
    }

    /**
     * Generate payment breakdown for each month
     */
    fun generatePaymentBreakdown(
        balance: Double,
        annualInterestRate: Double,
        monthlyPayment: Double,
        maxMonths: Int = 360 // 30 years max
    ): List<PaymentBreakdown> {
        val breakdown = mutableListOf<PaymentBreakdown>()
        var remainingBalance = balance
        val monthlyRate = annualInterestRate / 100 / 12
        var month = 1

        while (remainingBalance > 0.01 && month <= maxMonths) {
            val interestPayment = remainingBalance * monthlyRate
            val principalPayment = monthlyPayment - interestPayment

            // Adjust last payment if needed
            val actualPayment = if (principalPayment >= remainingBalance) {
                remainingBalance + interestPayment
            } else {
                monthlyPayment
            }

            val actualPrincipal = if (principalPayment >= remainingBalance) {
                remainingBalance
            } else {
                principalPayment
            }

            remainingBalance -= actualPrincipal

            breakdown.add(
                PaymentBreakdown(
                    month = month,
                    payment = actualPayment,
                    principalPaid = actualPrincipal,
                    interestPaid = interestPayment,
                    remainingBalance = if (remainingBalance < 0.01) 0.0 else remainingBalance
                )
            )

            month++
        }

        return breakdown
    }

    /**
     * Generate multiple payment scenarios for comparison
     */
    fun generatePaymentScenarios(loan: Loan): List<PaymentScenario> {
        val scenarios = mutableListOf<PaymentScenario>()

        // Scenario 1: Minimum payment
        val minMonths = calculateMonthsToPayoff(
            loan.currentBalance,
            loan.interestRate,
            loan.minimumPayment
        )
        val minInterest = calculateTotalInterest(
            loan.currentBalance,
            loan.interestRate,
            loan.minimumPayment
        )

        scenarios.add(
            PaymentScenario(
                scenarioName = "Minimum Payment",
                monthlyPayment = loan.minimumPayment,
                monthsToPayoff = minMonths,
                totalInterestPaid = minInterest,
                totalAmountPaid = loan.currentBalance + minInterest,
                monthlySavings = 0.0
            )
        )

        // Scenario 2: Minimum + R100
        val extra100 = loan.minimumPayment + 100
        scenarios.add(
            createScenario(
                "Min + R100",
                loan.currentBalance,
                loan.interestRate,
                extra100,
                minInterest
            )
        )

        // Scenario 3: Minimum + R250
        val extra250 = loan.minimumPayment + 250
        scenarios.add(
            createScenario(
                "Min + R250",
                loan.currentBalance,
                loan.interestRate,
                extra250,
                minInterest
            )
        )

        // Scenario 4: Minimum + R500
        val extra500 = loan.minimumPayment + 500
        scenarios.add(
            createScenario(
                "Min + R500",
                loan.currentBalance,
                loan.interestRate,
                extra500,
                minInterest
            )
        )

        // Scenario 5: Double payment
        val doublePayment = loan.minimumPayment * 2
        scenarios.add(
            createScenario(
                "Double Payment",
                loan.currentBalance,
                loan.interestRate,
                doublePayment,
                minInterest
            )
        )

        return scenarios
    }

    private fun createScenario(
        name: String,
        balance: Double,
        interestRate: Double,
        payment: Double,
        minInterest: Double
    ): PaymentScenario {
        val months = calculateMonthsToPayoff(balance, interestRate, payment)
        val interest = calculateTotalInterest(balance, interestRate, payment)

        return PaymentScenario(
            scenarioName = name,
            monthlyPayment = payment,
            monthsToPayoff = months,
            totalInterestPaid = interest,
            totalAmountPaid = balance + interest,
            monthlySavings = minInterest - interest
        )
    }

    /**
     * Calculate debt-free date
     */
    fun calculatePayoffDate(
        balance: Double,
        annualInterestRate: Double,
        monthlyPayment: Double
    ): String {
        val months = calculateMonthsToPayoff(balance, annualInterestRate, monthlyPayment)

        if (months == Int.MAX_VALUE) {
            return "Never (payment too low)"
        }

        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.MONTH, months)

        val dateFormat = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
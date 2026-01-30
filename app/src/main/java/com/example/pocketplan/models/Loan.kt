package com.pocketplan.models

data class Loan(
    val loanId: Int = 0,
    val loanName: String,
    val principalAmount: Double,
    val interestRate: Double, // Annual percentage rate
    val minimumPayment: Double,
    val currentBalance: Double,
    val userId: Int,
    val createdAt: String = "",
    // Calculated fields
    var monthsToPayoff: Int = 0,
    var totalInterestPaid: Double = 0.0,
    var totalAmountPaid: Double = 0.0
)
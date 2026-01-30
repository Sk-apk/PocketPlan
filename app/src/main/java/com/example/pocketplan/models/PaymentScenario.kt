package com.pocketplan.models

data class PaymentScenario(
    val scenarioName: String,
    val monthlyPayment: Double,
    val monthsToPayoff: Int,
    val totalInterestPaid: Double,
    val totalAmountPaid: Double,
    val monthlySavings: Double = 0.0 // Compared to minimum payment
)
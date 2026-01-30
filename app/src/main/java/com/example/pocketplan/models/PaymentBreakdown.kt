package com.pocketplan.models

data class PaymentBreakdown(
    val month: Int,
    val payment: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val remainingBalance: Double
)
package com.pocketplan.ui.loans

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pocketplan.R

class AddLoanDialog(
    private val onLoanAdded: (loanName: String, principal: Double, interestRate: Double, minPayment: Double, currentBalance: Double) -> Unit
) : DialogFragment() {

    private lateinit var etLoanName: EditText
    private lateinit var etPrincipal: EditText
    private lateinit var etInterestRate: EditText
    private lateinit var etMinPayment: EditText
    private lateinit var etCurrentBalance: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_loan, null)

        etLoanName = view.findViewById(R.id.etLoanName)
        etPrincipal = view.findViewById(R.id.etPrincipal)
        etInterestRate = view.findViewById(R.id.etInterestRate)
        etMinPayment = view.findViewById(R.id.etMinPayment)
        etCurrentBalance = view.findViewById(R.id.etCurrentBalance)

        return AlertDialog.Builder(requireContext())
            .setTitle("Add New Loan")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                addLoan()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun addLoan() {
        val loanName = etLoanName.text.toString().trim()
        val principalStr = etPrincipal.text.toString().trim()
        val interestRateStr = etInterestRate.text.toString().trim()
        val minPaymentStr = etMinPayment.text.toString().trim()
        val currentBalanceStr = etCurrentBalance.text.toString().trim()

        if (loanName.isEmpty() || principalStr.isEmpty() || interestRateStr.isEmpty() ||
            minPaymentStr.isEmpty() || currentBalanceStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val principal = principalStr.toDoubleOrNull()
        val interestRate = interestRateStr.toDoubleOrNull()
        val minPayment = minPaymentStr.toDoubleOrNull()
        val currentBalance = currentBalanceStr.toDoubleOrNull()

        if (principal == null || interestRate == null || minPayment == null || currentBalance == null) {
            Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        if (principal < 0 || interestRate < 0 || minPayment < 0 || currentBalance < 0) {
            Toast.makeText(requireContext(), "Values cannot be negative", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentBalance > principal) {
            Toast.makeText(requireContext(), "Current balance cannot exceed original principal", Toast.LENGTH_SHORT).show()
            return
        }

        onLoanAdded(loanName, principal, interestRate, minPayment, currentBalance)
    }
}
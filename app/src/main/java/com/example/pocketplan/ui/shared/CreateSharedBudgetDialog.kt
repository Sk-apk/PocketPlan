package com.pocketplan.ui.shared

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pocketplan.R

class CreateSharedBudgetDialog(
    private val onBudgetCreated: (budgetName: String) -> Unit
) : DialogFragment() {

    private lateinit var etBudgetName: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_shared_budget, null)

        etBudgetName = view.findViewById(R.id.etBudgetName)

        return AlertDialog.Builder(requireContext())
            .setTitle("Create Shared Budget")
            .setView(view)
            .setPositiveButton("Create") { _, _ ->
                createBudget()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun createBudget() {
        val budgetName = etBudgetName.text.toString().trim()

        if (budgetName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a budget name", Toast.LENGTH_SHORT).show()
            return
        }

        onBudgetCreated(budgetName)
    }
}
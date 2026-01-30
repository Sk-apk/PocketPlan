package com.pocketplan.ui.budget

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pocketplan.R
import com.pocketplan.models.Category

class SetCategoryBudgetDialog(
    private val categories: List<Category>,
    private val onBudgetSet: (category: Category, minAmount: Double, maxAmount: Double) -> Unit
) : DialogFragment() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var etMinAmount: EditText
    private lateinit var etMaxAmount: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_category_budget, null)

        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        etMinAmount = view.findViewById(R.id.etMinAmount)
        etMaxAmount = view.findViewById(R.id.etMaxAmount)

        // Setup category spinner
        val categoryNames = categories.map { it.categoryName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        return AlertDialog.Builder(requireContext())
            .setTitle("Set Category Budget")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                saveBudget()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun saveBudget() {
        val minStr = etMinAmount.text.toString().trim()
        val maxStr = etMaxAmount.text.toString().trim()

        if (minStr.isEmpty() || maxStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val minAmount = minStr.toDoubleOrNull()
        val maxAmount = maxStr.toDoubleOrNull()

        if (minAmount == null || maxAmount == null || minAmount < 0 || maxAmount < 0) {
            Toast.makeText(requireContext(), "Please enter valid amounts", Toast.LENGTH_SHORT).show()
            return
        }

        if (minAmount > maxAmount) {
            Toast.makeText(requireContext(), "Minimum cannot be greater than maximum", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPosition = spinnerCategory.selectedItemPosition
        val selectedCategory = categories[selectedPosition]

        onBudgetSet(selectedCategory, minAmount, maxAmount)
    }
}
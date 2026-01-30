package com.pocketplan.ui.categories

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pocketplan.R

class AddCategoryDialog(
    private val onCategoryAdded: (name: String, color: String, icon: String) -> Unit
) : DialogFragment() {

    private lateinit var etCategoryName: EditText
    private lateinit var rgColors: RadioGroup
    private lateinit var rgIcons: RadioGroup

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)

        etCategoryName = view.findViewById(R.id.etCategoryName)
        rgColors = view.findViewById(R.id.rgColors)
        rgIcons = view.findViewById(R.id.rgIcons)

        return AlertDialog.Builder(requireContext())
            .setTitle("Create New Pocket")
            .setView(view)
            .setPositiveButton("Create") { _, _ ->
                createCategory()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun createCategory() {
        val name = etCategoryName.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a pocket name", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedColorId = rgColors.checkedRadioButtonId
        if (selectedColorId == -1) {
            Toast.makeText(requireContext(), "Please select a color", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedIconId = rgIcons.checkedRadioButtonId
        if (selectedIconId == -1) {
            Toast.makeText(requireContext(), "Please select an icon", Toast.LENGTH_SHORT).show()
            return
        }

        val colorButton = view?.findViewById<RadioButton>(selectedColorId)
        val color = colorButton?.tag.toString()

        val iconButton = view?.findViewById<RadioButton>(selectedIconId)
        val icon = iconButton?.tag.toString()

        onCategoryAdded(name, color, icon)
    }
}
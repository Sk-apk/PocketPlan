package com.pocketplan.ui.expenses

import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pocketplan.R
import java.io.File

class ViewReceiptDialog(
    private val photoPath: String
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_view_receipt, null)

        val ivReceipt = view.findViewById<ImageView>(R.id.ivReceipt)

        // Load image from file
        try {
            val file = File(photoPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                ivReceipt.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Receipt Photo")
            .setView(view)
            .setPositiveButton("Close", null)
            .create()
    }
}
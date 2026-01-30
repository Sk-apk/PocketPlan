package com.pocketplan.ui.shared

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pocketplan.R

class InviteMemberDialog(
    private val onMemberInvited: (username: String) -> Unit
) : DialogFragment() {

    private lateinit var etUsername: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_invite_member, null)

        etUsername = view.findViewById(R.id.etUsername)

        return AlertDialog.Builder(requireContext())
            .setTitle("Invite Member")
            .setView(view)
            .setPositiveButton("Invite") { _, _ ->
                inviteMember()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun inviteMember() {
        val username = etUsername.text.toString().trim()

        if (username.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a username", Toast.LENGTH_SHORT).show()
            return
        }

        onMemberInvited(username)
    }
}
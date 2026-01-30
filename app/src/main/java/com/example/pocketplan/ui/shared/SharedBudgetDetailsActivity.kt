package com.pocketplan.ui.shared

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan.R
import com.pocketplan.database.SharedBudgetDao
import com.pocketplan.models.SharedBudgetMember
import com.pocketplan.utils.SessionManager

class SharedBudgetDetailsActivity : AppCompatActivity() {

    private lateinit var tvBudgetName: TextView
    private lateinit var tvMemberCount: TextView
    private lateinit var btnInviteMember: Button
    private lateinit var rvMembers: RecyclerView

    private lateinit var sharedBudgetDao: SharedBudgetDao
    private lateinit var sessionManager: SessionManager
    private lateinit var memberAdapter: SharedBudgetMemberAdapter

    private var members = mutableListOf<SharedBudgetMember>()
    private var sharedBudgetId = 0
    private var budgetName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_budget_details)

        supportActionBar?.title = "Shared Budget Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedBudgetId = intent.getIntExtra("SHARED_BUDGET_ID", 0)
        budgetName = intent.getStringExtra("SHARED_BUDGET_NAME") ?: ""

        initViews()
        sharedBudgetDao = SharedBudgetDao(this)
        sessionManager = SessionManager(this)

        tvBudgetName.text = budgetName

        setupRecyclerView()
        loadMembers()

        btnInviteMember.setOnClickListener {
            showInviteMemberDialog()
        }
    }

    private fun initViews() {
        tvBudgetName = findViewById(R.id.tvBudgetName)
        tvMemberCount = findViewById(R.id.tvMemberCount)
        btnInviteMember = findViewById(R.id.btnInviteMember)
        rvMembers = findViewById(R.id.rvMembers)
    }

    private fun setupRecyclerView() {
        memberAdapter = SharedBudgetMemberAdapter(
            members = members,
            onRemoveClick = { member ->
                removeMember(member)
            }
        )

        rvMembers.apply {
            layoutManager = LinearLayoutManager(this@SharedBudgetDetailsActivity)
            adapter = memberAdapter
        }
    }

    private fun loadMembers() {
        members.clear()
        members.addAll(sharedBudgetDao.getSharedBudgetMembers(sharedBudgetId))
        memberAdapter.notifyDataSetChanged()

        tvMemberCount.text = "${members.size} member${if (members.size != 1) "s" else ""}"
    }

    private fun showInviteMemberDialog() {
        val dialog = InviteMemberDialog { username ->
            inviteMember(username)
        }
        dialog.show(supportFragmentManager, "InviteMemberDialog")
    }

    private fun inviteMember(username: String) {
        // Get user ID by username
        val userId = sharedBudgetDao.getUserIdByUsername(username)

        if (userId == null) {
            Toast.makeText(this, "User '$username' not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if already a member
        if (members.any { it.userId == userId }) {
            Toast.makeText(this, "$username is already a member", Toast.LENGTH_SHORT).show()
            return
        }

        // Add member
        val success = sharedBudgetDao.addMember(sharedBudgetId, userId)

        if (success) {
            Toast.makeText(this, "$username has been invited!", Toast.LENGTH_SHORT).show()
            loadMembers()
        } else {
            Toast.makeText(this, "Failed to invite member", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeMember(member: SharedBudgetMember) {
        val currentUserId = sessionManager.getUserId()

        // Check if current user is owner
        if (!sharedBudgetDao.isOwner(sharedBudgetId, currentUserId)) {
            Toast.makeText(this, "Only the owner can remove members", Toast.LENGTH_SHORT).show()
            return
        }

        // Don't allow removing the owner
        if (member.isOwner) {
            Toast.makeText(this, "Cannot remove the owner", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Remove Member")
            .setMessage("Remove ${member.username} from this shared budget?")
            .setPositiveButton("Remove") { _, _ ->
                val success = sharedBudgetDao.removeMember(member.memberId)
                if (success) {
                    Toast.makeText(this, "${member.username} removed", Toast.LENGTH_SHORT).show()
                    loadMembers()
                } else {
                    Toast.makeText(this, "Failed to remove member", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
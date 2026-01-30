package com.pocketplan.ui.shared

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.pocketplan.R
import com.pocketplan.database.SharedBudgetDao
import com.pocketplan.models.SharedBudget
import com.pocketplan.utils.SessionManager

class SharedBudgetActivity : AppCompatActivity() {

    private lateinit var rvSharedBudgets: RecyclerView
    private lateinit var fabCreateSharedBudget: FloatingActionButton

    private lateinit var sharedBudgetDao: SharedBudgetDao
    private lateinit var sessionManager: SessionManager
    private lateinit var sharedBudgetAdapter: SharedBudgetAdapter

    private var sharedBudgets = mutableListOf<SharedBudget>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_budget)

        supportActionBar?.title = "Shared Budgets"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        sharedBudgetDao = SharedBudgetDao(this)
        sessionManager = SessionManager(this)

        setupRecyclerView()
        loadSharedBudgets()

        fabCreateSharedBudget.setOnClickListener {
            showCreateSharedBudgetDialog()
        }
    }

    private fun initViews() {
        rvSharedBudgets = findViewById(R.id.rvSharedBudgets)
        fabCreateSharedBudget = findViewById(R.id.fabCreateSharedBudget)
    }

    private fun setupRecyclerView() {
        sharedBudgetAdapter = SharedBudgetAdapter(
            sharedBudgets = sharedBudgets,
            onItemClick = { sharedBudget ->
                openSharedBudgetDetails(sharedBudget)
            },
            onDeleteClick = { sharedBudget ->
                deleteSharedBudget(sharedBudget)
            }
        )

        rvSharedBudgets.apply {
            layoutManager = LinearLayoutManager(this@SharedBudgetActivity)
            adapter = sharedBudgetAdapter
        }
    }

    private fun loadSharedBudgets() {
        val userId = sessionManager.getUserId()
        sharedBudgets.clear()
        sharedBudgets.addAll(sharedBudgetDao.getUserSharedBudgets(userId))
        sharedBudgetAdapter.notifyDataSetChanged()
    }

    private fun showCreateSharedBudgetDialog() {
        val dialog = CreateSharedBudgetDialog { budgetName ->
            createSharedBudget(budgetName)
        }
        dialog.show(supportFragmentManager, "CreateSharedBudgetDialog")
    }

    private fun createSharedBudget(budgetName: String) {
        val userId = sessionManager.getUserId()
        val sharedBudgetId = sharedBudgetDao.createSharedBudget(budgetName, userId)

        if (sharedBudgetId != -1L) {
            Toast.makeText(this, "Shared budget created!", Toast.LENGTH_SHORT).show()
            loadSharedBudgets()
        } else {
            Toast.makeText(this, "Failed to create shared budget", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSharedBudgetDetails(sharedBudget: SharedBudget) {
        val intent = android.content.Intent(this, SharedBudgetDetailsActivity::class.java)
        intent.putExtra("SHARED_BUDGET_ID", sharedBudget.sharedBudgetId)
        intent.putExtra("SHARED_BUDGET_NAME", sharedBudget.budgetName)
        startActivity(intent)
    }

    private fun deleteSharedBudget(sharedBudget: SharedBudget) {
        val userId = sessionManager.getUserId()

        // Check if user is owner
        if (!sharedBudgetDao.isOwner(sharedBudget.sharedBudgetId, userId)) {
            Toast.makeText(this, "Only the owner can delete this shared budget", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete Shared Budget")
            .setMessage("Are you sure you want to delete '${sharedBudget.budgetName}'? This will remove all shared data for all members.")
            .setPositiveButton("Delete") { _, _ ->
                val success = sharedBudgetDao.deleteSharedBudget(sharedBudget.sharedBudgetId)
                if (success) {
                    Toast.makeText(this, "Shared budget deleted", Toast.LENGTH_SHORT).show()
                    loadSharedBudgets()
                } else {
                    Toast.makeText(this, "Failed to delete shared budget", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        loadSharedBudgets()
    }
}
package com.pocketplan.ui.categories

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.pocketplan.R
import com.pocketplan.database.CategoryDao
import com.pocketplan.models.Category
import com.pocketplan.utils.SessionManager

class CategoriesActivity : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var fabAddCategory: FloatingActionButton

    private lateinit var categoryDao: CategoryDao
    private lateinit var sessionManager: SessionManager
    private lateinit var categoryAdapter: CategoryAdapter

    private var categories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        supportActionBar?.title = "My Pockets"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        categoryDao = CategoryDao(this)
        sessionManager = SessionManager(this)

        setupRecyclerView()
        loadCategories()

        fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun initViews() {
        rvCategories = findViewById(R.id.rvCategories)
        fabAddCategory = findViewById(R.id.fabAddCategory)
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            categories = categories,
            onDeleteClick = { category ->
                deleteCategory(category)
            }
        )

        rvCategories.apply {
            layoutManager = LinearLayoutManager(this@CategoriesActivity)
            adapter = categoryAdapter
        }
    }

    private fun loadCategories() {
        val userId = sessionManager.getUserId()
        categories.clear()
        categories.addAll(categoryDao.getAllCategories(userId))
        categoryAdapter.notifyDataSetChanged()
    }

    private fun showAddCategoryDialog() {
        val dialog = AddCategoryDialog { categoryName, color, icon ->
            addCategory(categoryName, color, icon)
        }
        dialog.show(supportFragmentManager, "AddCategoryDialog")
    }

    private fun addCategory(name: String, color: String, icon: String) {
        val userId = sessionManager.getUserId()

        if (categoryDao.isCategoryNameExists(name, userId)) {
            Toast.makeText(this, "Category already exists!", Toast.LENGTH_SHORT).show()
            return
        }

        val category = Category(
            categoryName = name,
            categoryColor = color,
            categoryIcon = icon,
            userId = userId
        )

        val success = categoryDao.addCategory(category)
        if (success) {
            Toast.makeText(this, "Pocket created successfully!", Toast.LENGTH_SHORT).show()
            loadCategories()
        } else {
            Toast.makeText(this, "Failed to create pocket", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteCategory(category: Category) {
        val success = categoryDao.deleteCategory(category.categoryId)
        if (success) {
            Toast.makeText(this, "Pocket deleted", Toast.LENGTH_SHORT).show()
            loadCategories()
        } else {
            Toast.makeText(this, "Failed to delete pocket", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
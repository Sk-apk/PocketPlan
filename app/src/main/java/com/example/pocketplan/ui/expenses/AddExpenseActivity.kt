package com.pocketplan.ui.expenses

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pocketplan.R
import com.pocketplan.database.CategoryDao
import com.pocketplan.database.ExpenseDao
import com.pocketplan.models.Category
import com.pocketplan.models.Expense
import com.pocketplan.utils.SessionManager
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var ivReceipt: ImageView
    private lateinit var btnAttachPhoto: Button
    private lateinit var btnSaveExpense: Button

    private lateinit var expenseDao: ExpenseDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var sessionManager: SessionManager

    private var categories = listOf<Category>()
    private var selectedDate = Calendar.getInstance()
    private var photoPath: String? = null

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        supportActionBar?.title = "Add Expense"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()
        expenseDao = ExpenseDao(this)
        categoryDao = CategoryDao(this)
        sessionManager = SessionManager(this)

        loadCategories()
        setupDatePicker()

        btnAttachPhoto.setOnClickListener {
            openImagePicker()
        }

        btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun initViews() {
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        ivReceipt = findViewById(R.id.ivReceipt)
        btnAttachPhoto = findViewById(R.id.btnAttachPhoto)
        btnSaveExpense = findViewById(R.id.btnSaveExpense)

        // Set today's date as default
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etDate.setText(dateFormat.format(selectedDate.time))
    }

    private fun loadCategories() {
        val userId = sessionManager.getUserId()
        categories = categoryDao.getAllCategories(userId)

        if (categories.isEmpty()) {
            Toast.makeText(this, "Please create a category first!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val categoryNames = categories.map { it.categoryName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun setupDatePicker() {
        etDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    etDate.setText(dateFormat.format(selectedDate.time))
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            imageUri?.let {
                ivReceipt.setImageURI(it)
                ivReceipt.visibility = ImageView.VISIBLE

                // Save image to internal storage
                photoPath = saveImageToInternalStorage(it)
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val filename = "receipt_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, filename)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveExpense() {
        val amountStr = etAmount.text.toString().trim()
        val date = etDate.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (amountStr.isEmpty()) {
            etAmount.error = "Please enter an amount"
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            etAmount.error = "Please enter a valid amount"
            return
        }

        if (description.isEmpty()) {
            etDescription.error = "Please enter a description"
            return
        }

        val selectedCategoryPosition = spinnerCategory.selectedItemPosition
        val selectedCategory = categories[selectedCategoryPosition]

        val userId = sessionManager.getUserId()

        val expense = Expense(
            amount = amount,
            date = date,
            description = description,
            categoryId = selectedCategory.categoryId,
            photoPath = photoPath,
            userId = userId
        )

        val success = expenseDao.addExpense(expense)

        if (success) {
            Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
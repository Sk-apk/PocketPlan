package com.pocketplan.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pocketplan.R
import com.pocketplan.database.UserDao
import com.pocketplan.utils.ValidationUtils

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvSignIn: TextView

    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        userDao = UserDao(this)

        btnRegister.setOnClickListener {
            registerUser()
        }

        tvSignIn.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvSignIn = findViewById(R.id.tvSignIn)
    }

    private fun registerUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validate username
        val (isUsernameValid, usernameError) = ValidationUtils.isValidUsername(username)
        if (!isUsernameValid) {
            etUsername.error = usernameError
            return
        }

        // Check if username exists
        if (userDao.isUsernameExists(username)) {
            etUsername.error = getString(R.string.error_username_exists)
            return
        }

        // Validate password
        val (isPasswordValid, passwordError) = ValidationUtils.isValidPassword(password)
        if (!isPasswordValid) {
            etPassword.error = passwordError
            return
        }

        // Check if passwords match
        val (doPasswordsMatch, matchError) = ValidationUtils.doPasswordsMatch(password, confirmPassword)
        if (!doPasswordsMatch) {
            etConfirmPassword.error = matchError
            return
        }

        // Register user
        val isRegistered = userDao.registerUser(username, password)

        if (isRegistered) {
            Toast.makeText(this, getString(R.string.success_registration), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, getString(R.string.error_registration_failed), Toast.LENGTH_SHORT).show()
        }
    }
}
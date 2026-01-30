package com.pocketplan.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pocketplan.MainActivity
import com.pocketplan.R
import com.pocketplan.database.UserDao
import com.pocketplan.utils.SessionManager
import com.pocketplan.utils.ValidationUtils

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignUp: TextView

    private lateinit var userDao: UserDao
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        userDao = UserDao(this)
        sessionManager = SessionManager(this)

        btnLogin.setOnClickListener {
            loginUser()
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)
    }

    private fun loginUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate inputs
        val (isUsernameValid, usernameError) = ValidationUtils.isValidUsername(username)
        if (!isUsernameValid) {
            etUsername.error = usernameError
            return
        }

        val (isPasswordValid, passwordError) = ValidationUtils.isValidPassword(password)
        if (!isPasswordValid) {
            etPassword.error = passwordError
            return
        }

        // Attempt login
        val user = userDao.loginUser(username, password)

        if (user != null) {
            sessionManager.createLoginSession(user)
            Toast.makeText(this, getString(R.string.success_login), Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show()
        }
    }
}
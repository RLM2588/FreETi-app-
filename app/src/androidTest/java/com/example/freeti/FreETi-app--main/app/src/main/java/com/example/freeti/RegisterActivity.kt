package com.example.freeti

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Инициализация всех элементов
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (username.isEmpty()) {
                etUsername.error = "Введите имя пользователя"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Введите email"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Введите пароль"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Пароль должен содержать минимум 6 символов"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                etConfirmPassword.error = "Пароли не совпадают"
                return@setOnClickListener
            }

            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.freeti.repository.AuthRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvRegister: TextView

    private val authRepository: AuthRepository by lazy {
        (application as MyApp).appContainer.authRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)

        // Запрашиваем фокус для поля имени пользователя
        etUsername.requestFocus()

        // Показываем клавиатуру автоматически
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etUsername, InputMethodManager.SHOW_IMPLICIT)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Валидация полей
            if (username.isEmpty() || password.isEmpty()) {
                if (username.isEmpty()) etUsername.error = "Введите имя пользователя"
                if (password.isEmpty()) etPassword.error = "Введите пароль"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // Вызываем метод репозитория
                val result = authRepository.login(username, password)

                result.onSuccess {
                    Toast.makeText(this@LoginActivity, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show()

                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                    // Переход на главный экран
                    startActivity(Intent(this@LoginActivity, MainScreen::class.java))
                    finish()
                }.onFailure { exception ->
                    Toast.makeText(this@LoginActivity, exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}
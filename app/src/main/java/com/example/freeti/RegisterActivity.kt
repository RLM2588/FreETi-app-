package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.freeti.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var userLogin: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPass: EditText
    private lateinit var button: Button
    private lateinit var tolog: Button
    private lateinit var buttonsendcode: Button
    private lateinit var userCode: EditText
    private lateinit var userPass2: EditText
    private val authRepository: AuthRepository by lazy {
        (application as MyApp).appContainer.authRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userLogin = findViewById(R.id.user_login)
        userEmail = findViewById(R.id.user_email)
        button = findViewById(R.id.button_reg)
        buttonsendcode = findViewById(R.id.send_code)
        userCode = findViewById(R.id.user_code)
        userPass = findViewById(R.id.user_pass)
        userPass2 = findViewById(R.id.user_pass2)
        tolog = findViewById(R.id.btnToLogin)

        tolog.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()
            val code = userCode.text.toString().trim()
            val pass2 = userPass2.text.toString().trim()

            if (pass != pass2) {
                userPass2.error = "Пароли не совпадают"
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass.length < 8) {
                userPass.error = "Пароль должен быть не менее 8 символов"
                Toast.makeText(
                    this,
                    "Пароль должен быть не менее 8 символов",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (login == "" || email == "" || code == ""){
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val answer = authRepository.finalRegister(login, email, code, pass)
                when (answer) {
                    "Succes" -> {
                        Toast.makeText(this@RegisterActivity, "Вы зарегистрировались", Toast.LENGTH_SHORT).show()
                        userLogin.text.clear()
                        userEmail.text.clear()
                        userPass.text.clear()
                        startActivity(Intent(this@RegisterActivity, MainScreen::class.java))
                        finish()
                    }
                    "Can not connect" -> {
                        Toast.makeText(this@RegisterActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this@RegisterActivity, "$answer", Toast.LENGTH_SHORT).show()
                        // Тут можно добавить обработку ошибок
                    }
                }
            }
        }

        buttonsendcode.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val login = userLogin.text.toString().trim()

            if (email.isBlank() || login.isBlank()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                val answer = authRepository.testRegister(login, email)

                when (answer) {
                    "OK" -> {
                        Toast.makeText(this@RegisterActivity, "Код отправлен на $email", Toast.LENGTH_SHORT).show()
                        buttonsendcode.text = "Отправить еще раз"
                    }
                    "Can not connect" -> {
                        Toast.makeText(this@RegisterActivity, "Ошибка соединения", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this@RegisterActivity, "$answer занято", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //fun is_code_correct(email: String,code: String): Boolean{
    //    // TODO тут будет проверка на верность кода
    //    return true
    //}
//
    //fun is_register_succes(): String{
    //    // TODO тут будет отправка всех данных на сервер
    //    return ""
    //}
}

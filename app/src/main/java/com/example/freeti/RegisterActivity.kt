package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var userLogin: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPass: EditText
    private lateinit var button: Button
    private lateinit var tolog: Button
    private lateinit var buttonsendcode: Button
    private lateinit var userCode: EditText
    private lateinit var userPass2: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userLogin = findViewById(R.id.user_login)
        userEmail = findViewById(R.id.user_email)
        button = findViewById(R.id.button_reg)
        buttonsendcode = findViewById(R.id.send_code)
        userCode = findViewById(R.id.user_code)
        userPass = findViewById(R.id.user_pass)
        userPass2 = findViewById(R.id.user_pass2)
        tolog = findViewById(R.id.tvToLogin)

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

            if (is_code_correct(email, code)) {
                userCode.error = "Неверный код"
                Toast.makeText(this, "Неверный код подтверждения", Toast.LENGTH_SHORT).show()
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

            if (login == "" || email == ""){
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val answer = is_register_succes();
            if(answer == ""){
                Toast.makeText(this, "Пользователь $login зарегистрирован", Toast.LENGTH_SHORT).show()
                //startActivity(Intent(this, MainScreen::class.java))

                userLogin.text.clear()
                userEmail.text.clear()
                userPass.text.clear()
                finish()
            }
            else {
                if (answer == "login_booked") {
                    Toast.makeText(this, "Логин занят", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else if (answer == "email_booked") {
                    Toast.makeText(this, "Почта занята", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
        }

        buttonsendcode.setOnClickListener {
            if (sendCode()) {
                Toast.makeText(this, "Код отправлен на почту", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                Toast.makeText(this, "Почта занята", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }

    fun sendCode(): Boolean {
        val email = userEmail.text.toString().trim()
        val code = buttonsendcode.text.toString().trim()

        if (email == "") {
            Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_SHORT).show()
        } else {
            // TODO сделать запрос на отправку кода
            Toast.makeText(this, "Код отправлен на $email", Toast.LENGTH_SHORT).show()
            buttonsendcode.text = "Отправить еще раз" // TODO убрать хардкод
            //buttonsendcode.isEnabled = true
            //buttonsendcode.requestFocus()
            return true
        }

        return false
    }

    fun is_code_correct(email: String,code: String): Boolean{
        // TODO тут будет проверка на верность кода
        return true
    }

    fun is_register_succes(): String{
        // TODO тут будет отправка всех данных на сервер
        return ""
    }
}

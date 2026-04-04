package com.example.freeti

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistrActivity : AppCompatActivity() {
    lateinit var userLogin: EditText
    lateinit var userEmail: EditText
    lateinit var userPass: EditText
    lateinit var button: Button
    lateinit var buttonsendcode : Button
    lateinit var userCode: EditText
    lateinit var userPass2: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registr)

        userLogin = findViewById(R.id.user_login)
        userEmail = findViewById(R.id.user_email)
        userPass = findViewById(R.id.user_pass)
        button = findViewById(R.id.button_reg)
        buttonsendcode = findViewById(R.id.send_code)
        userCode = findViewById(R.id.user_code)
        userPass2 = findViewById(R.id.user_pass2)

        val generatedCode = (100000..999999).random().toString()

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()
            val code = userCode.text.toString().trim()
            val pass2 = userPass2.text.toString().trim()

            if (pass != pass2) {
                userPass2.error = "Пароли не совпадают"
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_LONG).show()

            }

            if (code != generatedCode) {
                userCode.error = "Неверный код"
                Toast.makeText(this, "Неверный код подтверждения", Toast.LENGTH_LONG).show()


            if(login == "" || email == "" || pass == "")
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            else {
                //val user = User(login, email, pass)

                //val db = DbHelper(this, null)
                //db.addUser(user)
                Toast.makeText(this, "Пользователь $login добавлен", Toast.LENGTH_LONG).show()

            if(pass.length < 8) {
                userPass.error = "Пароль должен быть не менее 8 символов"
                Toast.makeText(this, "Пароль должен быть не менее 8 символов", Toast.LENGTH_LONG).show()
            }

                userLogin.text.clear()
                userEmail.text.clear()
                userPass.text.clear()
            }
        }
    }
    fun sendCode() {
        buttonsendcode.setOnClickListener {
            sendCode()
        }
        val login = userLogin.text.toString().trim()
        val email = userEmail.text.toString().trim()
        val code = buttonsendcode.text.toString().trim()

        if(login == "" || email == "") {//Отправка логина и почты и получение ответа от сервера
            Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
        }

        else {

            Toast.makeText(this, "Код отправлен на $email", Toast.LENGTH_LONG).show()
            buttonsendcode.isEnabled = true
            buttonsendcode.requestFocus()

        }
    }

}
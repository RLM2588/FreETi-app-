package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Находим элементусы
        val btnSearch = findViewById<Button>(R.id.btnSearch)
        val btnContacts = findViewById<Button>(R.id.btnContacts)
        val btnGroups = findViewById<Button>(R.id.btnGroups)
        val btnBack = findViewById<Button>(R.id.btnBack)

        // Кнопка Search → поиск других людей
        btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // Кнопка Contacts → избранное/друзья
        btnContacts.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }

        // Кнопка Groups → страница Егора Жывфыфыфыфы
        btnGroups.setOnClickListener {
            startActivity(Intent(this, GroupsActivity::class.java))
        }

        // Кнопка назад → вернуться на MainActivity
        btnBack.setOnClickListener {
            finish() // или это вроде startActivity + flags, но finish проще, так что похуй
        }

        // Нужно потом добавить редактирование имени/аватарки, но я пока не ебу как)
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val userName = findViewById<TextView>(R.id.userName)

        // Демо-данные (не привязаны к логину)
        userName.text = "Алексей" // можно заменить на любое имя
        // аватарку можно позже поменять, а то я в рот ебал думать
    }
}
package com.example.freeti

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FriendProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        // Находим элементы
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val ivAvatar = findViewById<ImageView>(R.id.iv_friend_avatar)
        val tvNickname = findViewById<TextView>(R.id.tv_friend_nickname)
        val tvName = findViewById<TextView>(R.id.tv_friend_name)
        val btnAddContact = findViewById<Button>(R.id.btn_add_contact)
        val btnMakeFriend = findViewById<Button>(R.id.btn_make_friend)

        // Получаем данные из Intent
        val nickname = intent.getStringExtra("nickname") ?: "unknown"
        val name = intent.getStringExtra("name") ?: "Неизвестный"

        // Заполняем поля
        tvNickname.text = "@$nickname"
        tvName.text = name
        // Здесь можно позже подгрузить аватарку ivAvatar.setImageResource(...)

        // Кнопка назад — просто закрываем экран
        btnBack.setOnClickListener {
            finish()
        }

        // Кнопка «Добавить в контакты» — пока заглушка
        btnAddContact.setOnClickListener {
            Toast.makeText(this, "Контакт добавлен", Toast.LENGTH_SHORT).show()
            // Здесь будет реальная логика (сохранение в БД)
        }

        // Кнопка «Сделать другом» — заглушка
        btnMakeFriend.setOnClickListener {
            Toast.makeText(this, "Запрос в друзья отправлен", Toast.LENGTH_SHORT).show()
            // Здесь будет логика добавления в друзья
        }
    }
}
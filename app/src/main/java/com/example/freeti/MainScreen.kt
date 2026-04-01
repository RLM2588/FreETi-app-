package com.example.freeti

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class MainScreen : AppCompatActivity() {
    lateinit var settings_button: TextView // потом возможно button
    lateinit var date_number: TextView
    lateinit var day_week: TextView
    lateinit var tasks_without_time: RecyclerView
    lateinit var tasks_view: RecyclerView
    lateinit var new_task: Button
    lateinit var privacy_button: Button
    val privacy_text: List<String> = listOf("Публичное", "Для друзей", "Приватное")
    val privacy_color: List<Long> = listOf(0xFFAA5555, 0xFF5555AA, 0xFF55AA55)
    var iterator_privacy = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализация
        settings_button = findViewById(R.id.main_settings)
        date_number = findViewById(R.id.main_date)
        day_week = findViewById(R.id.main_day_week)
        tasks_without_time = findViewById(R.id.main_tasks_without_time)
        tasks_view = findViewById(R.id.main_tasks)
        new_task = findViewById(R.id.main_new_task)
        privacy_button = findViewById(R.id.main_privacy)

        // Настройки / кнопка перехода в профиль
        settings_button.setOnClickListener {
            Toast.makeText(this, "Тут будет профиль", Toast.LENGTH_SHORT).show() //Удалить
            // TODO жду старницы профиль: startActivity(Intent(this, Profile::class.java))
        }

        // Переход в календарь
        date_number.setOnClickListener {
            Toast.makeText(this, "Тут будет календарь", Toast.LENGTH_SHORT).show() //Удалить
            // TODO Ожидает страницы календарь: startActivity(Intent(this, Profile::class.java))
        }

        // Кнопка приватности
        setPrivacy(false)
        privacy_button.setOnClickListener {
            setPrivacy()
        }

        // TODO на новую задачу открываем окно для создания
        // TODO для дня недели переход по неделе?
        // TODO хз еще где, но смена приватности
    }

    fun setPrivacy(k: Boolean = true) {
        if (k) {
            iterator_privacy = (iterator_privacy + 1) % 3
            Toast.makeText(this, privacy_text[iterator_privacy], Toast.LENGTH_SHORT).show()
        }
        privacy_button.setBackgroundColor(privacy_color[iterator_privacy].toInt())
        //privacy_button.setText(privacy_text[iterator_privacy])
        // TODO по менять сами задачи на нужные из бд
    }
}
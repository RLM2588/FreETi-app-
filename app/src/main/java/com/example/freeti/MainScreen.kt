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
import java.util.Calendar

class MainScreen : AppCompatActivity() {
    lateinit var settings_button: TextView // потом возможно button
    lateinit var date_number: TextView
    lateinit var day_week: TextView
    lateinit var tasks_without_time: RecyclerView
    lateinit var tasks_view: RecyclerView
    lateinit var new_task: Button
    lateinit var privacy_button: Button
    lateinit var month_and_year: TextView
    lateinit var calendar: Calendar

    // константы
    val privacy_text: List<String> = listOf("Публичное", "Для друзей", "Приватное")
    val week_text: List<String> = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    val monthes_text: List<String> = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль",
        "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
    val privacy_color: List<Long> = listOf(0xFFAA5555, 0xFF5555AA, 0xFF55AA55)

    // итераторы
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
        month_and_year = findViewById(R.id.main_month)

        calendar = Calendar.getInstance()

        setDate()

        // Настройки / кнопка перехода в профиль
        settings_button.setOnClickListener {
            Toast.makeText(this, "Тут будет профиль", Toast.LENGTH_SHORT).show() //Удалить
            // TODO жду старницы профиль: startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Переход в календарь
        date_number.setOnClickListener {
            Toast.makeText(this, "Тут будет календарь", Toast.LENGTH_SHORT).show() //Удалить
            // TODO Ожидает страницы календарь: startActivity(Intent(this, CalendarActivity::class.java))
        }

        month_and_year.setOnClickListener {
            Toast.makeText(this, "Тут будет календарь", Toast.LENGTH_SHORT).show() //Удалить
            // TODO Ожидает страницы календарь: startActivity(Intent(this, CalendarActivity::class.java))
        }

        // Кнопка приватности
        setPrivacy(false)
        privacy_button.setOnClickListener {
            setPrivacy()
        }

        // на новую задачу открываем окно для создания
        new_task.setOnClickListener {
            Toast.makeText(this, "Тут будет cтраница новой задачи", Toast.LENGTH_SHORT).show() //Удалить
            // TODO Ожидает страницы новая задача: startActivity(Intent(this, NewTaskActivity::class.java))
        }

        // Кнопка дня недели
        day_week.setOnClickListener {
            if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
                addDays(-6)
            }
            else {
                addDays(1)
            }
        }

        day_week.setOnLongClickListener {
            if (calendar.get(Calendar.DAY_OF_WEEK) == 2) {
                addDays(6)
            }
            else {
                addDays(-1)
            }
            true
        }

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

    fun setDate() {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val weekday = calendar.get(Calendar.DAY_OF_WEEK)

        date_number.text = day.toString()
        month_and_year.text = "${monthes_text[month]} $year"
        day_week.text = week_text[(weekday + 5) % 7]
    }

    fun addDays(delta: Int) {
        calendar.add(Calendar.DAY_OF_MONTH, delta)
        setDate()
    }
}
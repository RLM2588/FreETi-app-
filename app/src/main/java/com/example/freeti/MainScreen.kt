package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.data.local.entity.DTasks
import com.example.freeti.view_model.TasksViewModel
import com.example.freeti.view_model.TasksViewModelFactory
import com.example.freeti.views.TaskTimelineView
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class MainScreen : AppCompatActivity() {
    private lateinit var settings_button: TextView // потом возможно button
    private lateinit var date_number: TextView
    private lateinit var day_week: TextView
    private lateinit var tasks_without_time: RecyclerView
    private lateinit var tasks_view: TaskTimelineView
    private lateinit var new_task: Button
    private lateinit var privacy_button: Button
    private lateinit var main_refresh_button: Button
    private lateinit var month_and_year: TextView
    private lateinit var calendar: Calendar

    private lateinit var viewModel: TasksViewModel

     // константы
    private val privacy_text: List<String> = listOf("Публичное", "Для друзей", "Приватное")
    private val privacy_text_ENUM: List<String> = listOf("PUBLIC", "FRIENDS", "PRIVATE")
    private val week_text: List<String> = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    private val monthes_text: List<String> = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль",
        "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
    private val privacy_color: List<Long> = listOf(0xFFAA5555, 0xFF5555AA, 0xFF55AA55)

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
        val app = application as MyApp
        viewModel = ViewModelProvider(
            this,
            TasksViewModelFactory(app.appContainer.myTasksDao, app.appContainer.taskSyncManager)
        ).get(TasksViewModel::class.java)

        viewModel.addTestTasks()

        settings_button = findViewById(R.id.main_settings)
        date_number = findViewById(R.id.main_date)
        day_week = findViewById(R.id.main_day_week)
        tasks_without_time = findViewById(R.id.main_tasks_without_time)
        tasks_view = findViewById(R.id.main_tasks)
        new_task = findViewById(R.id.main_new_task)
        privacy_button = findViewById(R.id.main_privacy)
        month_and_year = findViewById(R.id.main_month)
        main_refresh_button = findViewById(R.id.main_refresh)

        calendar = Calendar.getInstance()

        setDate()

        viewModel.setDate(calendar.timeInMillis)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasksForDay.collect { tasks ->
                    updateTasksList(tasks)
                }
            }
        }

        // Настройки
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasksForDay.collect { tasks ->
                    tasks_view.setTasks(tasks)
                }
            }
        }
        // клик
        tasks_view.onTaskClickListener = object : TaskTimelineView.OnTaskClickListener {
            override fun onTaskClick(task: DTasks) {
                val intent = Intent(this@MainScreen, EditTaskActivity::class.java)
                intent.putExtra("task_id", task.id)
                startActivity(intent)
            }
        }

        main_refresh_button.setOnClickListener {
            viewModel.forceRefresh()
        }
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
            showMaterialDatePicker()
            //Toast.makeText(this, "Тут будет календарь", Toast.LENGTH_SHORT).show() //Удалить
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

    private fun updateTasksList(tasks: List<DTasks>) {
        // обновляем адаптер
    }

    private fun getCurrentYearMonth(): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return "$year-$month"
    }

    private fun setPrivacy(k: Boolean = true) {
        if (k) {
            iterator_privacy = (iterator_privacy + 1) % 3
            Toast.makeText(this, privacy_text[iterator_privacy], Toast.LENGTH_SHORT).show()
        }
        privacy_button.setBackgroundColor(privacy_color[iterator_privacy].toInt())
        viewModel.setPrivacy(privacy_text_ENUM[iterator_privacy])
    }

    private fun setDate() {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val weekday = calendar.get(Calendar.DAY_OF_WEEK)

        date_number.text = day.toString()
        month_and_year.text = "${monthes_text[month]} $year"
        day_week.text = week_text[(weekday + 5) % 7]
        viewModel.setDate(calendar.timeInMillis)
    }

    private fun addDays(delta: Int) {
        calendar.add(Calendar.DAY_OF_MONTH, delta)
        setDate()
    }

    private fun showMaterialDatePicker() {
        // Создаем "строитель" диалога выбора даты
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату")               // Заголовок
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Текущая дата по умолчанию
            .build()

        // Устанавливаем слушатель нажатия кнопки "ОК"
        datePicker.addOnPositiveButtonClickListener { selection ->
            // selection — это выбранная дата в миллисекундах от начала эпохи (UTC)
            val calendar1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar1.timeInMillis = selection

            // Обновляем ваш календарь (переменную calendar)
            this.calendar.timeInMillis = selection
            // Обновляем текст на экране
            setDate()
            // Опционально: показываем Toast с выбранной датой
            Toast.makeText(this, "Выбрано: ${date_number.text}.${month_and_year.text}", Toast.LENGTH_SHORT).show()
        }

        // Показываем диалог
        datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER_TAG")
    }
}
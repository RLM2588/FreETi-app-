package com.example.freeti

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.freeti.data.local.entity.DTasks
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class NewTaskActivity : AppCompatActivity() {
    private var taskId: String? = null
    private var day: Long? = null
    private var startTime = Calendar.getInstance() // TimeZone.getTimeZone("UTC")
    private var endTime = Calendar.getInstance()
    private lateinit var t_title: EditText
    private lateinit var t_body: EditText
    private lateinit var t_importance: Spinner
    private lateinit var t_delete: Button
    private lateinit var t_mode: TextView
    private lateinit var t_et_color: EditText
    private lateinit var t_no_time: CheckBox
    private lateinit var task_without_time: CheckBox
    private lateinit var t_is_done: CheckBox
    private lateinit var t_date_start: Button
    private lateinit var t_time_start: Button
    private lateinit var t_date_end: Button
    private lateinit var t_time_end: Button
    private lateinit var t_move: Button
    private lateinit var t_layout_end: LinearLayout
    private lateinit var task_times: LinearLayout
    private lateinit var t_view_color_preview: View
    private lateinit var t_ok: Button
    private lateinit var t_save: Button
    private lateinit var t_privacy: com.google.android.material.button.MaterialButton
    private lateinit var t_esc: FloatingActionButton

    private val privacy_text: List<String> by lazy {
        listOf(
            getString(R.string.privacy_public),
            getString(R.string.privacy_friends),
            getString(R.string.privacy_private)
        )
    }
    private val privacy_text_ENUM: List<String> = listOf("PUBLIC", "FRIENDS", "PRIVATE")
    private val privacy_color: List<Long> = listOf(0xFFa76a6b, 0xFF617d9a, 0xFF6aa776)

    private val privacy_icons: List<Int> = listOf(
        R.drawable.outline_globe_24,   // для PUBLIC
        R.drawable.baseline_groups_24,  // для FRIENDS
        R.drawable.baseline_person_24      // для PRIVATE
    )

    var iterator_privacy = 2
    var is_plus_day = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        t_title = findViewById(R.id.task_title)
        t_body = findViewById(R.id.task_body)
        t_importance = findViewById(R.id.task_importance)
        t_delete = findViewById(R.id.task_delete)
        t_mode = findViewById(R.id.task_title_mode)
        t_et_color  = findViewById(R.id.task_et_color)
        t_no_time = findViewById(R.id.task_no_time)
        t_date_start = findViewById(R.id.task_date_start)
        t_time_start = findViewById(R.id.task_time_start)
        t_date_end = findViewById(R.id.task_date_end)
        t_time_end = findViewById(R.id.task_time_end)
        t_layout_end = findViewById(R.id.task_layout_end)
        t_view_color_preview = findViewById(R.id.task_view_color_preview)
        t_ok = findViewById(R.id.task_ok)
        t_save = findViewById(R.id.task_save)
        t_privacy = findViewById(R.id.task_privacy)
        t_esc = findViewById(R.id.task_esc)
        t_is_done = findViewById(R.id.task_is_done)
        t_move = findViewById(R.id.task_move)
        task_without_time = findViewById(R.id.task_without_time)
        task_times = findViewById(R.id.task_times)

        val white_color = findViewById<View>(R.id.white_color)
        val red_color = findViewById<View>(R.id.red_color)
        val green_color = findViewById<View>(R.id.green_color)
        val blue_color = findViewById<View>(R.id.blue_color)
        val grbl_color = findViewById<View>(R.id.grbl_color)
        val gray_color = findViewById<View>(R.id.gray_color)

        taskId = intent.getStringExtra("task_id")
        day = intent.getLongExtra("daytime", System.currentTimeMillis())

        if (taskId != null) {
            t_mode.text = "Редактирование"
            t_delete.visibility = View.VISIBLE
            loadTaskForEdit(taskId!!)
        } else {
            setPrivacy(false)
            day?.let { val dayLong = it
                startTime.apply { timeInMillis = dayLong
                    set(Calendar.HOUR_OF_DAY, 9)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                endTime.apply {
                    timeInMillis = dayLong
                    set(Calendar.HOUR_OF_DAY, 10)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                updateStartDisplay()
                updateEndDisplay()
            }
        }

        t_privacy.setOnClickListener {
            setPrivacy()
        }

        t_esc.setOnClickListener { finish() }

        t_date_start.setOnClickListener { pickDate(startTime) { updateStartDisplay() } }
        t_time_start.setOnClickListener { pickTime(startTime) { updateStartDisplay() } }
        t_date_end.setOnClickListener { pickDate(endTime) { updateEndDisplay() } }
        t_time_end.setOnClickListener { pickTime(endTime) { updateEndDisplay() } }

        t_no_time.setOnCheckedChangeListener { _, isChecked ->
            t_layout_end.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        task_without_time.setOnCheckedChangeListener { _, isChecked ->
            t_no_time.visibility = if (isChecked) View.GONE else View.VISIBLE
            task_times.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        t_move.setOnClickListener {
            if (is_plus_day) {
                startTime.apply { add(Calendar.DAY_OF_MONTH, 1)  }
                endTime.apply { add(Calendar.DAY_OF_MONTH, 1)  }
            } else {
                startTime.apply { add(Calendar.DAY_OF_MONTH, -1)  }
                endTime.apply { add(Calendar.DAY_OF_MONTH, -1)  }
            }
            updateStartDisplay()
            updateEndDisplay()
        }

        t_move.setOnLongClickListener {
            is_plus_day = !is_plus_day
            t_move.text = if(is_plus_day) getString(R.string.newtask_plusday) else getString(R.string.newtask_minusday)
            true
        }

        ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("Простая", "Важная", "Крайне важная")).also {
            adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            t_importance.adapter = adapter
        }

        t_et_color.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hex = s?.toString() ?: "FFFFFF"
                val color = try {
                    Color.parseColor("#$hex")
                } catch (e: IllegalArgumentException) {
                    Color.LTGRAY
                }
                t_view_color_preview.setBackgroundColor(color)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        white_color.setOnClickListener { t_et_color.setText("FFFFFF") }
        red_color.setOnClickListener { t_et_color.setText("BB3344") }
        green_color.setOnClickListener { t_et_color.setText("22AA55") }
        blue_color.setOnClickListener { t_et_color.setText("3355AB") }
        grbl_color.setOnClickListener { t_et_color.setText("33AAAA") }
        gray_color.setOnClickListener { t_et_color.setText("888888") }

        t_ok.setOnClickListener {
            val ttext = t_title.text
            if (ttext.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ttext.length > 20) {
                Toast.makeText(this, "Название должно быть меньше 20 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveTask()
        }

        t_save.setOnClickListener {
            val ttext = t_title.text
            if (ttext.isEmpty()) {
                Toast.makeText(this, "Введите название", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ttext.length > 20) {
                Toast.makeText(this, "Название должно быть меньше 20 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveTask()

            finish()
        }

        t_delete.setOnClickListener {
            taskId?.let { id ->
                lifecycleScope.launch {
                    val task = (application as MyApp).appContainer.myTasksDao.getTaskById(id)
                    task?.let {
                        val deleted = it.copy(status = "DELETED", updated_at = System.currentTimeMillis(), is_synced = false)
                        (application as MyApp).appContainer.myTasksDao.insertAll(listOf(deleted))
                        finish()
                    }
                }
            }
        }
    }

    private fun saveTask() {
        val title = t_title.text.toString().trim()
        val body = t_body.text.toString().trim()
        val colorHex = t_et_color.text.toString().trim().ifBlank { "FFFFFF" }
        val isNoTime = t_no_time.isChecked
        val isTime = task_without_time.isChecked
        val importance = t_importance.selectedItemPosition + 1

        val finalStart = if (isTime) 0L else startTime.timeInMillis
        val finalEnd = if (isNoTime || isTime) 0L else maxOf(finalStart + 600_000L, endTime.timeInMillis)

        val task = DTasks(
            id = taskId ?: UUID.randomUUID().toString(),
            title = title,
            body = body,
            start = finalStart,
            time_end = finalEnd,
            status = if(t_is_done.isChecked) "DONE" else "ACTIVE",
            privacy = privacy_text_ENUM[iterator_privacy],
            importance = importance,
            push_template_id = 1, //TODO потом добавить уведомления
            colour = colorHex,
            updated_at = System.currentTimeMillis(),
            is_synced = false
        )

        lifecycleScope.launch {
            //if (isNew)

            (application as MyApp).appContainer.myTasksDao.insertAll(listOf(task))
        }

        Toast.makeText(this, "Задача сохранена", Toast.LENGTH_SHORT).show()
    }

    private fun setPrivacy(k: Boolean = true) {
        if (k) {
            iterator_privacy = (iterator_privacy + 1) % 3
        }

        // Меняем текст
        t_privacy.text = privacy_text[iterator_privacy]

        // Меняем цвет фона
        t_privacy.setBackgroundColor(privacy_color[iterator_privacy].toInt())

        // Меняем иконку
        t_privacy.setIconResource(privacy_icons[iterator_privacy])

        if (k) {
            Toast.makeText(this, privacy_text[iterator_privacy], Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTaskForEdit(id: String) {
        lifecycleScope.launch {
            val task = (application as MyApp).appContainer.myTasksDao.getTaskById(id)
            if (task != null) {
                t_title.setText(task.title)
                t_body.setText(task.body)
                t_importance.setSelection(task.importance - 1)
                val text_ptivacy = task.privacy

                if (text_ptivacy == "PUBLIC") {
                    iterator_privacy = 0
                } else if (text_ptivacy == "FRIENDS") {
                    iterator_privacy = 1
                } else {
                    iterator_privacy = 2
                }
                t_is_done.isChecked = if (task.status == "ACTIVE") false else true
                t_et_color.setText(task.colour)
                if(task.start == 0L) {
                    task_without_time.isChecked = true
                    t_no_time.visibility = View.GONE
                    task_times.visibility = View.GONE
                }
                else if (task.time_end == 0L) {
                    startTime.timeInMillis = task.start
                    endTime.timeInMillis = task.start + 3600_000L // fallback
                    t_no_time.isChecked = true
                } else {
                    startTime.timeInMillis = task.start
                    endTime.timeInMillis = task.time_end
                    t_no_time.isChecked = false
                }
                setPrivacy(false)
                updateStartDisplay()
                updateEndDisplay()
            } else {
                Toast.makeText(this@NewTaskActivity, "Задача не найдена", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateStartDisplay() {
        t_date_start.text = formatDate(startTime)
        t_time_start.text = formatTime(startTime)
    }

    private fun updateEndDisplay() {
        t_date_end.text = formatDate(endTime)
        t_time_end.text = formatTime(endTime)
        if (endTime.get(Calendar.HOUR_OF_DAY) == 0 && endTime.get(Calendar.MINUTE) == 0) {
            endTime.set(Calendar.HOUR_OF_DAY, 24)
        }
    }

    private fun pickDate(calendar: Calendar, onSet: () -> Unit) {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                onSet()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun pickTime(calendar: Calendar, onSet: () -> Unit) {
        TimePickerDialog(this, { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            onSet()
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun formatDate(cal: Calendar) = String.format("%02d.%02d.%04d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH)+1, cal.get(Calendar.YEAR))
    private fun formatTime(cal: Calendar) = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
}
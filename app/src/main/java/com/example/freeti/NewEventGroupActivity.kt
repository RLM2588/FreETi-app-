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
import com.example.freeti.network_entity.NGroupEventSearch
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class NewEventGroupActivity : AppCompatActivity() {
    private var day: Long? = null
    private var startTime = Calendar.getInstance() // TimeZone.getTimeZone("UTC")
    private var endTime = Calendar.getInstance()
    private lateinit var t_title: EditText
    private lateinit var t_body: EditText
    private lateinit var t_importance: Spinner
    private lateinit var t_mode: TextView
    private lateinit var t_et_color: EditText
    private lateinit var t_date_start: Button
    private lateinit var t_time_start: Button
    private lateinit var t_date_end: Button
    private lateinit var t_time_end: Button
    private lateinit var t_layout_end: LinearLayout
    private lateinit var t_view_color_preview: View
    private lateinit var t_save: Button
    private lateinit var t_esc: FloatingActionButton
    private lateinit var groupId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_event_group)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        t_title = findViewById(R.id.group_task_title)
        t_body = findViewById(R.id.group_task_body)
        t_importance = findViewById(R.id.group_task_importance)
        t_mode = findViewById(R.id.group_task_title_mode)
        t_et_color  = findViewById(R.id.group_task_et_color)
        t_date_start = findViewById(R.id.group_task_date_start)
        t_time_start = findViewById(R.id.group_task_time_start)
        t_date_end = findViewById(R.id.group_task_date_end)
        t_time_end = findViewById(R.id.group_task_time_end)
        t_layout_end = findViewById(R.id.group_task_layout_end)
        t_view_color_preview = findViewById(R.id.group_task_view_color_preview)
        t_save = findViewById(R.id.group_task_save)
        t_esc = findViewById(R.id.group_task_esc)

        groupId = intent.getStringExtra("groupId")?: "0"
        if (groupId == "0") finish()

        day = intent.getLongExtra("daytime", System.currentTimeMillis())

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

        t_esc.setOnClickListener { finish() }

        t_date_start.setOnClickListener { pickDate(startTime) { updateStartDisplay() } }
        t_time_start.setOnClickListener { pickTime(startTime) { updateStartDisplay() } }
        t_date_end.setOnClickListener { pickDate(endTime) { updateEndDisplay() } }
        t_time_end.setOnClickListener { pickTime(endTime) { updateEndDisplay() } }

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
            sendTask()

            finish()
        }
    }

    private fun sendTask() {
        val title = t_title.text.toString().trim()
        val body = t_body.text.toString().trim()
        val colorHex = t_et_color.text.toString().trim().ifBlank { "FFFFFF" }
        val importance = t_importance.selectedItemPosition + 1

        val group_task = NGroupEventSearch(
            title = title,
            body = body,
            day_start = formatDate(startTime),
            day_end = formatDate(endTime),
            time_start = formatTime(startTime),
            time_end = formatTime(endTime),
            importance = importance,
            colour = colorHex
        )

        lifecycleScope.launch {
            if((application as MyApp).appContainer.groupTaskRepository.sendTasks(groupId, group_task)) {
                Toast.makeText(this@NewEventGroupActivity, "Успешно", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@NewEventGroupActivity, "Не получилось", Toast.LENGTH_SHORT).show()
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
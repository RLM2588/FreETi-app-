package com.example.freeti

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CreateEventActivity : AppCompatActivity() {


    private lateinit var spinnerMode: Spinner
    private lateinit var editTextEventName: EditText
    private lateinit var buttonSubmit: Button


    private lateinit var layoutCreateMode: LinearLayout
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextDuration: EditText
    private lateinit var spinnerPriority: Spinner


    private lateinit var layoutSearchMode: LinearLayout
    private lateinit var editTextDateStart: EditText
    private lateinit var editTextDateEnd: EditText
    private lateinit var editTextTimeStart: EditText
    private lateinit var editTextTimeEnd: EditText

    private var isCreateMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        initViews()
        setupModeSpinner()
        setupPrioritySpinner()
        setupListeners()
    }

    private fun initViews() {
        spinnerMode = findViewById(R.id.spinnerMode)
        editTextEventName = findViewById(R.id.editTextEventName)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        layoutCreateMode = findViewById(R.id.layoutCreateMode)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextDuration = findViewById(R.id.editTextDuration)
        spinnerPriority = findViewById(R.id.spinnerPriority)

        layoutSearchMode = findViewById(R.id.layoutSearchMode)
        editTextDateStart = findViewById(R.id.editTextDateStart)
        editTextDateEnd = findViewById(R.id.editTextDateEnd)
        editTextTimeStart = findViewById(R.id.editTextTimeStart)
        editTextTimeEnd = findViewById(R.id.editTextTimeEnd)
    }

    private fun setupModeSpinner() {
        val modes = listOf("Создать событие", "Найти свободное время")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMode.adapter = adapter

        spinnerMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                isCreateMode = position == 0
                layoutCreateMode.visibility = if (isCreateMode) View.VISIBLE else View.GONE
                layoutSearchMode.visibility = if (isCreateMode) View.GONE else View.VISIBLE
                buttonSubmit.text = if (isCreateMode) "Создать событие" else "Найти время"
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupPrioritySpinner() {
        val priorities = listOf("Низкий", "Средний", "Высокий")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = adapter
    }

    private fun setupListeners() {
        buttonSubmit.setOnClickListener {
            if (isCreateMode) createEvent() else searchTime()
        }
    }

    private fun createEvent() {
        val name = editTextEventName.text.toString().trim()
        val date = editTextDate.text.toString().trim()
        val time = editTextTime.text.toString().trim()
        val duration = editTextDuration.text.toString().trim()
        val priority = spinnerPriority.selectedItem.toString()

        if (name.isEmpty()) {
            editTextEventName.error = "Введите название"
            return
        }
        if (date.isEmpty()) {
            editTextDate.error = "Введите дату"
            return
        }
        if (time.isEmpty()) {
            editTextTime.error = "Введите время"
            return
        }
        if (duration.isEmpty()) {
            editTextDuration.error = "Введите длительность"
            return
        }

        Toast.makeText(
            this,
            "Событие \"$name\" создано!\n$date $time\n$duration мин\nПриоритет: $priority",
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    private fun searchTime() {
        val name = editTextEventName.text.toString().trim()
        val dateStart = editTextDateStart.text.toString().trim()
        val dateEnd = editTextDateEnd.text.toString().trim()
        val timeStart = editTextTimeStart.text.toString().trim()
        val timeEnd = editTextTimeEnd.text.toString().trim()

        if (name.isEmpty()) {
            editTextEventName.error = "Введите название встречи"
            return
        }
        if (dateStart.isEmpty()) {
            editTextDateStart.error = "Введите дату начала"
            return
        }
        if (dateEnd.isEmpty()) {
            editTextDateEnd.error = "Введите дату конца"
            return
        }
        if (timeStart.isEmpty()) {
            editTextTimeStart.error = "Введите время начала"
            return
        }
        if (timeEnd.isEmpty()) {
            editTextTimeEnd.error = "Введите время конца"
            return
        }

        Toast.makeText(
            this,
            "Ищем встречу \"$name\"\nс $dateStart по $dateEnd\nс $timeStart до $timeEnd",
            Toast.LENGTH_LONG
        ).show()
        finish()
    }
}
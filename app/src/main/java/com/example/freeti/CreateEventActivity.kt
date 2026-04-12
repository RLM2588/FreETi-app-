package com.example.freeti

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CreateEventActivity : AppCompatActivity() {

    private lateinit var editTextEventName: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextDuration: EditText
    private lateinit var spinnerPriority: Spinner
    private lateinit var buttonCreateEvent: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        initViews()
        setupPrioritySpinner()
        setupListeners()
    }

    private fun initViews() {
        editTextEventName = findViewById(R.id.editTextEventName)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextDuration = findViewById(R.id.editTextDuration)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        buttonCreateEvent = findViewById(R.id.buttonCreateEvent)
    }

    private fun setupPrioritySpinner() {
        val priorities = listOf("Низкий", "Средний", "Высокий")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = adapter
    }

    private fun setupListeners() {
        buttonCreateEvent.setOnClickListener {
            createEvent()
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
}
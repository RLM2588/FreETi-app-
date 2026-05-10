package com.example.freeti

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreatePollActivity : AppCompatActivity() {

    private lateinit var optionsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_poll)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val editQuestion = findViewById<EditText>(R.id.editTextQuestion)
        optionsContainer = findViewById(R.id.optionsContainer)
        val btnAddOption = findViewById<Button>(R.id.btnAddOption)
        val btnCreate = findViewById<Button>(R.id.btnCreatePoll)

        btnBack.setOnClickListener { finish() }

        // Добавляем два пустых поля
        addOptionField("")
        addOptionField("")

        btnAddOption.setOnClickListener {
            addOptionField("")
        }

        btnCreate.setOnClickListener {
            val question = editQuestion.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(this, "Введите вопрос", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val optionTexts = mutableListOf<String>()
            for (i in 0 until optionsContainer.childCount) {
                val child = optionsContainer.getChildAt(i)
                val editText = child.findViewById<EditText>(R.id.editTextOption)
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) {
                    optionTexts.add(text)
                }
            }

            if (optionTexts.size < 2) {
                Toast.makeText(this, "Нужно минимум 2 варианта", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val options = optionTexts.map { Option(text = it) }.toMutableList()
            val poll = Poll(question = question, options = options)
            PollsRepository.addPoll(poll)
            finish()
        }
    }

    private fun addOptionField(text: String) {
        val inflater = LayoutInflater.from(this)
        val optionView = inflater.inflate(R.layout.item_option_edit, optionsContainer, false)
        val editText = optionView.findViewById<EditText>(R.id.editTextOption)
        editText.setText(text)

        val btnDeleteOption = optionView.findViewById<ImageButton>(R.id.btnDeleteOption)
        btnDeleteOption.setOnClickListener {
            if (optionsContainer.childCount > 2) {
                optionsContainer.removeView(optionView)
            } else {
                Toast.makeText(this, "Должно быть минимум 2 варианта", Toast.LENGTH_SHORT).show()
            }
        }
        optionsContainer.addView(optionView)
    }
}
package com.example.freeti

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PollDetailActivity : AppCompatActivity() {

    private lateinit var poll: Poll
    private lateinit var radioGroup: RadioGroup
    private lateinit var resultsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poll_detail)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        radioGroup = findViewById(R.id.radioGroupOptions)
        val btnVote = findViewById<Button>(R.id.btnVote)
        resultsLayout = findViewById(R.id.resultsLayout)

        val pollId = intent.getStringExtra("poll_id") ?: return finish()
        poll = PollsRepository.polls.find { it.id == pollId } ?: return finish()

        btnBack.setOnClickListener { finish() }
        tvQuestion.text = poll.question

        // Создаём радиокнопки для вариантов
        radioGroup.removeAllViews()
        for (option in poll.options) {
            val radioButton = RadioButton(this).apply {
                text = option.text
                tag = option.id
            }
            radioGroup.addView(radioButton)
        }

        // Проверяем, голосовал ли уже пользователь
        val hasVoted = PollsRepository.votedPollIds.contains(poll.id)
        if (hasVoted) {
            btnVote.isEnabled = false
            btnVote.text = "Вы уже проголосовали"
            showResults()
        } else {
            btnVote.setOnClickListener {
                val selectedId = radioGroup.checkedRadioButtonId
                if (selectedId == -1) {
                    Toast.makeText(this, "Выберите вариант", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val radioButton = findViewById<RadioButton>(selectedId)
                val optionId = radioButton.tag as String
                val success = PollsRepository.vote(poll.id, optionId)
                if (success) {
                    Toast.makeText(this, "Голос учтён", Toast.LENGTH_SHORT).show()
                    btnVote.isEnabled = false
                    btnVote.text = "Вы уже проголосовали"
                    showResults()
                } else {
                    Toast.makeText(this, "Ошибка голосования", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showResults() {
        resultsLayout.removeAllViews()
        for (option in poll.options) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_option_result, resultsLayout, false)
            val tvOptionText = view.findViewById<TextView>(R.id.tvOptionText)
            val tvVotes = view.findViewById<TextView>(R.id.tvVotes)
            tvOptionText.text = option.text
            tvVotes.text = "${option.votes} голос(ов)"
            resultsLayout.addView(view)
        }
    }
}
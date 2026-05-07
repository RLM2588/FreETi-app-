package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PollsListActivity : AppCompatActivity() {

    private lateinit var adapter: PollAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_polls_list)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnAdd = findViewById<Button>(R.id.btnAddPoll)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPolls)

        btnBack.setOnClickListener { finish() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PollAdapter(
            polls = PollsRepository.polls,
            onPollClick = { poll ->
                val intent = Intent(this, PollDetailActivity::class.java)
                intent.putExtra("poll_id", poll.id)
                startActivity(intent)
            },
            onDeleteClick = { poll ->
                AlertDialog.Builder(this)
                    .setTitle("Удалить опрос?")
                    .setMessage("Вы уверены, что хотите удалить \"${poll.question}\"?")
                    .setPositiveButton("Удалить") { _, _ ->
                        PollsRepository.deletePoll(poll.id)
                        adapter.updateList(PollsRepository.polls)
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
        )
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            startActivity(Intent(this, CreatePollActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateList(PollsRepository.polls) // обновление при возврате
    }
}
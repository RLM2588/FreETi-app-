package com.example.freeti

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    // Это пародия на базу данных. Формально мы должны брать из сервера, но сервера пока нет(
    private val allUsers = listOf(
        User("nikita", "Никита"),
        User("nikolay", "Николай"),
        User("alex", "Алексей"),
        User("alexandra", "Александра"),
        User("maria", "Мария"),
        User("mariya", "Мария"),
        User("john", "Джон"),
        User("jonny", "Джонни"),
        User("olga", "Ольга"),
        User("pavel", "Павел"),
        User("anna", "Анна"),
        User("andrey", "Андрей")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        val hintTextView = findViewById<TextView>(R.id.textViewHint)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewResults)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Адаптер изначально с пустым списком
        val adapter = UserAdapter(emptyList()) { user ->
            Toast.makeText(this, "Выбран: ${user.name} (${user.nickname})", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val prefix = s.toString().trim().lowercase()
                if (prefix.isEmpty()) {
                    hintTextView.visibility = android.view.View.GONE
                    recyclerView.visibility = android.view.View.GONE
                    adapter.updateList(emptyList())
                    return
                }

                val filtered = allUsers.filter { it.nickname.startsWith(prefix) }
                if (filtered.isNotEmpty()) {
                    hintTextView.visibility = android.view.View.VISIBLE
                    recyclerView.visibility = android.view.View.VISIBLE
                    adapter.updateList(filtered)
                } else {
                    hintTextView.visibility = android.view.View.GONE
                    recyclerView.visibility = android.view.View.GONE
                    adapter.updateList(emptyList())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
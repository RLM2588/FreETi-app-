package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    // Тестовые данные (позже заменишь на серверные)
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

        // Адаптер: при клике открываем FriendProfileActivity
        val adapter = UserAdapter(emptyList()) { user ->
            val intent = Intent(this, FriendProfileActivity::class.java)
            intent.putExtra("nickname", user.nickname)
            intent.putExtra("name", user.name)
            startActivity(intent)
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
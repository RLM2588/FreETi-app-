package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactsActivity : AppCompatActivity() {

    private val allUsers = listOf(
        User("example", "Пример Контакта"),
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

    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val editTextSearch = findViewById<EditText>(R.id.editTextSearchContacts)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewContacts)

        btnBack.setOnClickListener {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UserAdapter(allUsers) { user ->
            val intent = Intent(this, FriendProfileActivity::class.java)
            intent.putExtra("nickname", user.nickname)
            intent.putExtra("name", user.name)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Активный поиск: фильтрует список по мере ввода
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val prefix = s.toString().trim().lowercase()
                val filtered = if (prefix.isEmpty()) {
                    allUsers
                } else {
                    allUsers.filter { it.nickname.startsWith(prefix) }
                }
                adapter.updateList(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
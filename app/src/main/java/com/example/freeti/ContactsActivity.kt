package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.adapters_pack.ContactsAdapter
import com.example.freeti.repository.ContactsRepository
import com.example.freeti.view_model.ContactsViewModel
import com.example.freeti.view_model.ContactsViewModelFactory
import kotlinx.coroutines.launch
import kotlin.jvm.java

class ContactsActivity : AppCompatActivity() {
    private lateinit var viewModel: ContactsViewModel
    private lateinit var adapter: ContactsAdapter
    private var groupId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        groupId = intent.getStringExtra("group_id")
        val btnBack = findViewById<ImageButton>(R.id.btn_back_contacts)
        btnBack.setOnClickListener {
            finish()
        }

        val appContainer = (application as MyApp).appContainer
        val repository = ContactsRepository(
            appContainer.contactsDao,
            appContainer.userDao,
            appContainer.apiService,
            appContainer.tokenManager
        )
        viewModel = ViewModelProvider(this, ContactsViewModelFactory(repository))
            .get(ContactsViewModel::class.java)

        val editSearch = findViewById<EditText>(R.id.editTextSearchContacts)
        val recycler = findViewById<RecyclerView>(R.id.recyclerViewContacts)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = ContactsAdapter(emptyList(),
            onItemClick = { item ->
                // переход в профиль друга
                val intent = Intent(this, FriendProfileActivity::class.java)
                intent.putExtra("other_id", item.id)
                startActivity(intent)
            },
            onItemLongClick = if (groupId != null) {
                { item -> addToGroup(item.id) }
            } else null
        )
        recycler.adapter = adapter

        lifecycleScope.launch {
            viewModel.contacts.collect { adapter.updateList(it) }
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.search(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun addToGroup(userId: Int) {
        lifecycleScope.launch {
            try {
                val appContainer = (application as MyApp).appContainer
                appContainer.apiService.addMember(userId, groupId!!)
                Toast.makeText(this@ContactsActivity, "Добавлен в группу", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ContactsActivity, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
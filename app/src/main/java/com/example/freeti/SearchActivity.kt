package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freeti.adapters_pack.UserAdapter
import com.example.freeti.data.local.entity.DUsers
import com.example.freeti.repository.SearchRepository
import com.example.freeti.view_model.SearchViewModel
import com.example.freeti.view_model.SearchViewModelFactory
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val appContainer = (application as MyApp).appContainer
        val repository = SearchRepository(
            appContainer.userDao,
            appContainer.apiService,
            appContainer.tokenManager
        )
        val factory = SearchViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        val hintTextView = findViewById<TextView>(R.id.textViewHint)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewResults)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = UserAdapter(emptyList()) { user ->
            Toast.makeText(this, "Выбран: ${user.username} (@${user.login})", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this@SearchActivity, FriendProfileActivity::class.java)
            intent.putExtra("other_id", user.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.users.collect { userList ->
                adapter.updateList(userList)
                if (userList.isEmpty()) {
                    hintTextView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                } else {
                    hintTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
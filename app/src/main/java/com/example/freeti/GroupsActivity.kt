package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Button
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.example.freeti.adapters_pack.GroupAdapter
import com.example.freeti.repository.GroupRepository
import kotlinx.coroutines.launch

class GroupsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var fabAddGroup: FloatingActionButton
    private lateinit var groupAdapter: GroupAdapter
    private val groupRepository: GroupRepository by lazy {
        (application as MyApp).appContainer.groupRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        recyclerView = findViewById(R.id.recyclerViewGroups)
        searchEditText = findViewById(R.id.editTextSearch)
        fabAddGroup = findViewById(R.id.fabAddGroup)

        setupRecyclerView()
        setupSearch()
        setupFab()

        lifecycleScope.launch {
            groupRepository.syncGroups()

            groupRepository.allGroups.collect { groups ->
                val query = searchEditText.text.toString()
                val filtered = if (query.isEmpty()) groups
                else groups.filter { it.title.contains(query, ignoreCase = true) }
                groupAdapter.updateList(filtered)
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        groupAdapter = GroupAdapter(emptyList()) { group ->
            val intent = Intent(this, GroupDetailsActivity::class.java)
            intent.putExtra("group_id", group.id)
            startActivity(intent)
        }
        recyclerView.adapter = groupAdapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lifecycleScope.launch {
                    groupRepository.allGroups.collect { groups ->
                        val filtered = if (s.isNullOrEmpty()) groups
                        else groups.filter { it.title.contains(s, ignoreCase = true) }
                        groupAdapter.updateList(filtered)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFab() {
        fabAddGroup.setOnClickListener {
            showAddGroupDialog()
        }
    }

    private fun showAddGroupDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_group, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextGroupName)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnConfirm.setOnClickListener {
            val name = editTextName.text.toString().trim()
            if (name.isNotEmpty()) {
                addGroup(name)
                dialog.dismiss()
            } else {
                editTextName.error = "Введите название"
            }
        }

        dialog.show()
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.7f)
            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun addGroup(name: String) {
        lifecycleScope.launch {
            try {
                groupRepository.addGroup(name)
                Toast.makeText(this@GroupsActivity, "Группа \"$name\" добавлена", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@GroupsActivity, "Ошибка при добавлении", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
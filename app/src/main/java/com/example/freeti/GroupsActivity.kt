package com.example.freeti

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

class GroupsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var fabAddGroup: FloatingActionButton
    private lateinit var groupAdapter: GroupAdapter
    private val allGroups = mutableListOf<Group>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        initViews()
        setupRecyclerView()
        setupSearch()
        setupFab()

    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewGroups)
        searchEditText = findViewById(R.id.editTextSearch)
        fabAddGroup = findViewById(R.id.fabAddGroup)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        groupAdapter = GroupAdapter(allGroups) { group ->
            Toast.makeText(this, "Открыта группа: ${group.name}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = groupAdapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterGroups(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun filterGroups(query: String) {
        allGroups.add(Group("first", "MyNiggers", "epstein",6)) //  потом убрать, для теста
        allGroups.add(Group("1first", "1MyNiggers", "1epstein",6)) //  потом убрать, для теста
        val filtered = if (query.isEmpty()) {
            allGroups
        } else {
            allGroups.filter { group ->
                group.name.contains(query, ignoreCase = true)
            }
        }
        groupAdapter.updateList(filtered)
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


        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

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
        val newGroup = Group(
            id = System.currentTimeMillis().toString(),
            name = name
        )
        allGroups.add(newGroup)
        filterGroups(searchEditText.text.toString())
        Toast.makeText(this, "Группа \"$name\" добавлена", Toast.LENGTH_SHORT).show()
    }
}


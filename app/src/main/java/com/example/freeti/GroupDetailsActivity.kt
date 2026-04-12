package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var textGroupName: TextView
    private lateinit var textDescription: TextView
    private lateinit var textMemberCount: TextView
    private lateinit var buttonCreateEvent: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)

        initViews()
        setupToolbar()
        loadGroupData()
        setupListeners()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        textGroupName = findViewById(R.id.textGroupName)
        textDescription = findViewById(R.id.textDescription)
        textMemberCount = findViewById(R.id.textMemberCount)
        buttonCreateEvent = findViewById(R.id.buttonCreateEvent)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadGroupData() {
        val group = intent.getSerializableExtra("group") as? Group
        if (group != null) {
            textGroupName.text = group.name
            textDescription.text = group.description.takeIf { it.isNotEmpty() } ?: "Нет описания"
            textMemberCount.text = "${group.memberCount} участников"
        }
    }

    private fun setupListeners() {
        buttonCreateEvent.setOnClickListener {
            Toast.makeText(this, "Создание события", Toast.LENGTH_SHORT).show()
        }
    }
}
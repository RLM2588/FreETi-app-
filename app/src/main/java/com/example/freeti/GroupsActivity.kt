package com.example.freeti

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GroupsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        findViewById<Button>(R.id.btnBackFromGroups).setOnClickListener {
            finish()
        }
    }
}
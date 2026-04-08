package com.example.freeti

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ContactsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        findViewById<Button>(R.id.btnBackFromContacts).setOnClickListener {
            finish()
        }
    }
}
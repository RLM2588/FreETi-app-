package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var main_screen : TextView
    private lateinit var reg_butt : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        main_screen = findViewById(R.id.main_screen_text_button)
        reg_butt = findViewById(R.id.register_text_button)
        reg_butt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        main_screen.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
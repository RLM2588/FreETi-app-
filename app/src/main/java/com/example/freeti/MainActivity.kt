package com.example.freeti

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        pref = getSharedPreferences("settings", MODE_PRIVATE)
        if(pref.getBoolean("AutoToMain", false)) {startActivity(Intent(this, MainScreen::class.java))}

        autoMain()
    }

    override fun onResume(){
        super.onResume()
        autoMain()
    }

    fun autoMain() {
        if(MyApp.container.tokenManager.hasSession()) {
            startActivity(Intent(this, MainScreen::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
package com.example.freeti

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    private lateinit var s_check: CheckBox
    private lateinit var s_auto: CheckBox
    private lateinit var s_esc: Button
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        s_check = findViewById(R.id.settings_test)
        s_auto = findViewById(R.id.settings_auto)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        pref = getSharedPreferences("settings", MODE_PRIVATE)

        s_check.isChecked = pref.getBoolean("noTestAdd", false)
        s_auto.isChecked = pref.getBoolean("AutoToMain", false)

        // Сохраняем изменения
        s_check.setOnCheckedChangeListener { _, isChecked ->
            pref.edit().putBoolean("noTestAdd", isChecked).apply()
        }
        s_auto.setOnCheckedChangeListener { _, isChecked ->
            pref.edit().putBoolean("AutoToMain", isChecked).apply()
        }
    }
}
package com.example.freeti

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var s_check: CheckBox
    private lateinit var s_auto: CheckBox
    private lateinit var s_esc: Button
    private lateinit var pref: SharedPreferences

    private lateinit var logOutButton: Button

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
        s_esc = findViewById(R.id.settings_esc)
        logOutButton = findViewById(R.id.logOutButton)

        pref = getSharedPreferences("settings", MODE_PRIVATE)

        s_check.isChecked = pref.getBoolean("noTestAdd", false)
        s_auto.isChecked = pref.getBoolean("AutoToMain", false)


        s_check.setOnCheckedChangeListener { _, _ -> pref.edit().putBoolean("noTestAdd", s_check.isChecked).apply() }
        s_auto.setOnCheckedChangeListener { _, _ -> pref.edit().putBoolean("AutoToMain", s_auto.isChecked).apply() }

        s_esc.setOnClickListener { finish() }
        logOutButton.setOnClickListener {
            (application as MyApp).appContainer.tokenManager.clearTokens()
            startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
            finish()
        }
    }



}
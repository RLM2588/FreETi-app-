package com.example.freeti

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.freeti.tokens.TokenManager

class SettingsActivity : AppCompatActivity() {
    private lateinit var s_check: CheckBox
    private lateinit var s_auto: CheckBox
    private lateinit var s_esc: Button
    private lateinit var s_logout: Button
    private lateinit var pref: SharedPreferences
    private val t_meneger: TokenManager by lazy {(application as MyApp).appContainer.tokenManager}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //if (!t_meneger.hasSession()) {
        //    Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
        //    finish()
        //}

        s_check = findViewById(R.id.settings_test)
        s_auto = findViewById(R.id.settings_auto)
        s_esc = findViewById(R.id.settings_esc)
        s_logout = findViewById(R.id.settings_logout)

        pref = getSharedPreferences("settings", MODE_PRIVATE)

        s_check.isChecked = pref.getBoolean("noTestAdd", false)
        s_auto.isChecked = pref.getBoolean("AutoToMain", false)


        s_check.setOnCheckedChangeListener { _, _ -> pref.edit().putBoolean("noTestAdd", s_check.isChecked).apply() }
        s_auto.setOnCheckedChangeListener { _, _ -> pref.edit().putBoolean("AutoToMain", s_auto.isChecked).apply() }

        s_esc.setOnClickListener { finish() }

        s_logout.setOnClickListener{
            t_meneger.clearTokens()
        }
    }
}
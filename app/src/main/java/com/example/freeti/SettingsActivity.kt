package com.example.freeti

import android.content.Intent
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
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var s_check: CheckBox
    private lateinit var s_auto: CheckBox
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

        s_check = findViewById(R.id.settings_test)
        s_auto = findViewById(R.id.settings_auto)
        s_logout = findViewById(R.id.settings_logout)
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
        s_logout.setOnClickListener{
            t_meneger.clearTokens()
            lifecycleScope.launch {
                (application as MyApp).appContainer.database.clearAll_Tables()
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }



}
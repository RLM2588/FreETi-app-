package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var main_screen : TextView
    private lateinit var test_out : EditText
    private lateinit var test_out_button : Button
    private lateinit var test_inp : TextView
    private lateinit var test_inp_button : Button

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
        test_out = findViewById(R.id.test_text_to_send)
        test_out_button = findViewById(R.id.test_send)
        test_inp = findViewById(R.id.test_input_text)
        test_inp_button = findViewById(R.id.test_input)

        test_out_button.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val result = MyApp.container.testRepository.send(test_out.text.toString())
                result.onSuccess {
                    test_inp.text = it
                }.onFailure {
                    Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            } // TODO пока так для теста, но будущие запросы через ViewModel
        }

        test_inp_button.setOnClickListener {
            Toast.makeText(this, "Пока только одна кнопка для теста", Toast.LENGTH_SHORT).show()
        }

        main_screen.setOnClickListener {
            startActivity(Intent(this, MainScreen::class.java))
        }
    }
}
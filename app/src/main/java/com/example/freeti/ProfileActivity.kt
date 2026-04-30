package com.example.freeti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.freeti.data.local.entity.DUsers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var btnSearch: Button
    private lateinit var btnContacts: Button
    private lateinit var btnGroups: Button
    private lateinit var btnBack: Button
    private lateinit var btnSave: Button
    private lateinit var btnSetting: Button
    private lateinit var userName: TextView
    private lateinit var userAvatar: TextView
    private var currentUser: DUsers? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Находим элементусы
        btnSearch = findViewById(R.id.btnSearch)
        btnContacts = findViewById(R.id.btnContacts)
        btnGroups = findViewById(R.id.btnGroups)
        btnBack = findViewById(R.id.btnBack)
        userName = findViewById(R.id.userName)
        btnSave = findViewById(R.id.btnSave)
        btnSetting = findViewById(R.id.btnSettings)
        userAvatar = findViewById(R.id.faceInput)

        val app = application as MyApp
        val userDao = app.appContainer.userDao
        val apiService = app.appContainer.apiService
        val tokenManager = app.appContainer.tokenManager

        val userId = tokenManager.getUserId()

        //btnSearch.setOnClickListener {
        //    startActivity(Intent(this, SearchActivity::class.java))
        //}
//
        //// Кнопка Contacts → избранное/друзья
        //btnContacts.setOnClickListener {
        //    startActivity(Intent(this, ContactsActivity::class.java))
        //}
//
        //// Кнопка Groups → страница Егора Ж
        //btnGroups.setOnClickListener {
        //    startActivity(Intent(this, GroupsActivity::class.java))
        //}

        btnSetting.setOnClickListener {
            //
        }

        // Кнопка назад → вернуться на MainActivity
        btnBack.setOnClickListener {
            finish() // или это вроде startActivity + flags, но finish проще, так что похуй
        }

        btnSave.setOnClickListener {
            val newUsername = userName.text.toString().trim()
            val newAvatar = userAvatar.text.toString().trim()

            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedUser = currentUser?.copy(
                username = newUsername,
                avatar = newAvatar,
                login = tokenManager.getLogin()
            ) ?: DUsers(id = userId, login = tokenManager.getLogin(), username = newUsername, avatar = newAvatar)

            lifecycleScope.launch {
                try {
                    userDao.insertAll(listOf(updatedUser))
                    currentUser = updatedUser

                    val response = apiService.updateUser(userId, updatedUser.toNetworkEntity())
                    if (response.isSuccessful) {
                        response.body()?.let { serverUser ->
                            val serverEntity = DUsers(
                                id = serverUser.id,
                                login = serverUser.login,
                                username = serverUser.username,
                                avatar = serverUser.avatar
                            )
                            userDao.insertAll(listOf(serverEntity))
                            currentUser = serverEntity
                        }
                    }
                } catch (e: Exception) {}
            }
        }
        lifecycleScope.launch {
            var user = userDao.getUserForId(userId)
            if (user == null) {
                val defaultUser = DUsers(id = userId, login = "uniqlogin", username = "User", avatar = ":)")
                userDao.insertAll(listOf(defaultUser))
                currentUser = defaultUser

                try {
                    val response = apiService.updateUser(userId, defaultUser.toNetworkEntity())
                    if (response.isSuccessful) {
                        response.body()?.let { serverUser ->
                            val serverEntity = DUsers(
                                id = serverUser.id,
                                login = serverUser.login,
                                username = serverUser.username,
                                avatar = serverUser.avatar
                            )
                            userDao.insertAll(listOf(serverEntity))
                            currentUser = serverEntity
                        }
                    }
                } catch (e: Exception) {
                }
            } else {
                currentUser = user
            }

            // Заполняем поля
            userName.setText(currentUser?.username ?: "")
            userAvatar.setText(currentUser?.avatar ?: "")
        }
    }
}
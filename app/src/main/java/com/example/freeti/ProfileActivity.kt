package com.example.freeti

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.freeti.data.local.entity.DUsers
import com.example.freeti.network_entity.UpdateResponse
import kotlinx.coroutines.launch
import kotlin.math.min

class ProfileActivity : AppCompatActivity() {
    private lateinit var btnSearch: Button
    private lateinit var btnContacts: Button
    private lateinit var btnGroups: Button
    private lateinit var btnBack: android.widget.ImageButton
    private lateinit var btnSave: Button
    private lateinit var btnSetting: Button
    private lateinit var btnChangeAvatar: Button // Новая кнопка
    private lateinit var userName: EditText
    private lateinit var userAvatar: EditText // Изменено на EditText для управления фокусом
    private var currentUser: DUsers? = null

    private lateinit var updateView: TextView

    private lateinit var updateResp: UpdateResponse
    private val app = application as MyApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Находим элементы
        btnSearch = findViewById(R.id.btnSearch)
        btnContacts = findViewById(R.id.btnContacts)
        btnGroups = findViewById(R.id.btnGroups)
        btnBack = findViewById(R.id.btnBack)
        userName = findViewById(R.id.userName)
        btnSave = findViewById(R.id.btnSave)
        btnSetting = findViewById(R.id.btnSettings)
        userAvatar = findViewById(R.id.faceInput)
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar) // Инициализация
        updateView = findViewById(R.id.main_update)
        updateView.visibility = View.GONE

        val app = application as MyApp
        val userDao = app.appContainer.userDao
        val apiService = app.appContainer.apiService
        val tokenManager = app.appContainer.tokenManager

        //if (!tokenManager.hasSession()) {
        //    Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
        //    finish()
        //}

        val userId = tokenManager.getUserId()

        // Логика кнопки изменения аватара
        //btnChangeAvatar.setOnClickListener {
        //    // Разрешаем редактирование
        //    userAvatar.isFocusable = true
        //    userAvatar.isFocusableInTouchMode = true
        //    userAvatar.isCursorVisible = true
        //    userAvatar.requestFocus()
        //
        //    // Принудительно показываем клавиатуру
        //    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //    imm.showSoftInput(userAvatar, InputMethodManager.SHOW_IMPLICIT)
        //}
        btnChangeAvatar.setOnClickListener {
            // Делаем поле редактируемым
            userAvatar.isFocusableInTouchMode = true
            userAvatar.isFocusable = true
            userAvatar.isCursorVisible = true

            // Переносим фокус на поле аватара
            userAvatar.requestFocus()

            // Показываем клавиатуру
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(userAvatar, InputMethodManager.SHOW_IMPLICIT)

            // Выделяем текст, чтобы сразу можно было стереть старый
            userAvatar.selectAll()
            if (userAvatar.rotation == 0f) userAvatar.setText("_" + userAvatar.text.toString())
        }
        btnBack.setOnClickListener { finish() }


        btnSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // Кнопка Contacts → избранное/друзья
        btnContacts.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }

        btnGroups.setOnClickListener {
            startActivity(Intent(this, GroupsActivity::class.java))
        }

        btnSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Кнопка назад → вернуться на MainActivity
        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            val newUsername = userName.text.toString().trim()
            val newAvatar = userAvatar.text.toString().trim()

            userAvatar.isFocusable = false
            userAvatar.isFocusableInTouchMode = false
            userAvatar.isCursorVisible = false
            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // После сохранения снова отключаем фокус аватара, чтобы избежать случайных нажатий
            userAvatar.isFocusable = false
            userAvatar.isFocusableInTouchMode = false
            userAvatar.isCursorVisible = false

            val updatedUser = currentUser?.copy(
                username = newUsername,
                avatar = newAvatar,
                login = tokenManager.getLogin()
            ) ?: DUsers(
                id = userId,
                login = tokenManager.getLogin(),
                username = newUsername,
                avatar = newAvatar
            )
            Log.d("updatedUser", "current: ${tokenManager.getLogin()}")

            UpdateRequest() //TODO: Ждет реализации на сервере

            lifecycleScope.launch {
                try {
                    val userResp = apiService.get_username_id()
                    if (userResp.isSuccessful && userResp.body() != null && userResp.body()?.username != null) {
                        Log.d(
                            "login changed",
                            "old: ${updatedUser.login}, new: ${userResp.body()?.username}"
                        )
                        tokenManager.saveUser(userResp.body()?.userId, userResp.body()?.username)
                        updatedUser.login = userResp.body()?.username.toString();
                    }
                    Log.d("f", "b")
                    userDao.insertAll(listOf(updatedUser))
                    currentUser = updatedUser

                    val response = apiService.updateUser(updatedUser.toNetworkEntity())
                    if (response.isSuccessful) {
                        response.body()?.let { serverUser ->
                            val serverEntity = DUsers(
                                id = serverUser.id,
                                login = serverUser.login,
                                username = serverUser.username?: "no name",
                                avatar = serverUser.avatar?: ":|"
                            )
                            userDao.insertAll(listOf(serverEntity))
                            currentUser = serverEntity
                        }
                    }
                } catch (e: Exception) {
                }
                setInfo()
            }
        }
        lifecycleScope.launch {
            val user = userDao.getUserForId(userId)
            if (user == null) {
                val defaultUser =
                    DUsers(id = userId, login = "uniqlogin", username = "User", avatar = ":)")
                userDao.insertAll(listOf(defaultUser))
                currentUser = defaultUser

                try {
                    var resp = apiService.getUser()
                    var response = resp
                    if (!response.isSuccessful) {
                        response = apiService.updateUser(defaultUser.toNetworkEntity())
                    }

                    if (response.isSuccessful) {
                        response.body()?.let { serverUser ->
                            val serverEntity = DUsers(
                                id = serverUser.id,
                                login = serverUser.login,
                                username = serverUser.username?: "no name",
                                avatar = serverUser.avatar?: ":|"
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
            setInfo()
        }

        updateView.setOnClickListener {
            showUpdateDialog(updateResp)
        }
    }

    private fun showUpdateDialog(response: UpdateResponse) {
        if(response.status != "critical") {
            AlertDialog.Builder(this)
                .setTitle("Доступна новая версия ${response.latestVersion}")
                .setMessage(response.releaseNotes ?: "Нажмите для обновления")
                .setPositiveButton("Обновить") { _, _ ->
                    openBrowser(response.downloadUrl)
                }
                .setNegativeButton("Позже") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Необходимо обновление")
                .setMessage("Доступна версия ${response.latestVersion}. К сожалению ваша версия больше не поддерживается.\n" + (response.releaseNotes ?: "Нажмите для обновления"))
                .setPositiveButton("Обновить") { _, _ ->
                    openBrowser(response.downloadUrl)
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun openBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            // Если нет браузера или ссылка некорректна
            Toast.makeText(this, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show()
        }
    }

    private fun UpdateRequest() {
        lifecycleScope.launch {
            val answer = app.appContainer.updateRepository.checkUpdate(app.currentVersion)
            if (answer != null) {
                updateResp = answer
                if (answer.status != "ok") {
                    updateView.visibility = View.VISIBLE
                    if (answer.status == "critical") {
                        showUpdateDialog(answer)
                    }
                }
            }
        }
    }

    private fun setInfo() {
        var textAvatar = currentUser?.avatar ?: ""
        if (textAvatar.length > 1 && textAvatar[0] == '_') {
            userAvatar.rotation = 0f
            textAvatar = textAvatar.substring(1, textAvatar.length)
        } else {
            userAvatar.rotation = 90f
            textAvatar = textAvatar.substring(0, min(textAvatar.length, 4))
        }
        userName.setText(currentUser?.username ?: "")
        userAvatar.setText(textAvatar)//.trim()
    }
}
package com.example.freeti

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.freeti.data.local.entity.DUsers
import com.example.freeti.repository.ContactsRepository
import com.example.freeti.view_model.OtherTasksViewModel
import com.example.freeti.view_model.OtherTasksViewModelFactory
import com.example.freeti.views.OtherTaskTimelineView
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class FriendProfileActivity : AppCompatActivity() {
    private lateinit var date_number: TextView
    private lateinit var day_week: TextView
    private lateinit var tasks_view: OtherTaskTimelineView

    private lateinit var privacy_button: com.google.android.material.button.MaterialButton
    private lateinit var month_and_year: TextView
    private lateinit var avatar: TextView
    private lateinit var calendar: Calendar
    private lateinit var viewModel: OtherTasksViewModel
    private lateinit var nextDayButton: ImageButton
    private lateinit var prevDayButton: ImageButton

    private val privacy_text_ENUM = listOf("PUBLIC", "FRIENDS")
    private val week_text = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    private val monthes_text = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
    private val privacy_color = listOf(0xFFa76a6b.toInt(), 0xFF617d9a.toInt())
    private val privacy_icons = listOf(
        R.drawable.outline_globe_24,   // для PUBLIC
        R.drawable.baseline_groups_24  // для FRIENDS
    )

    private val privacy_labels: List<String> by lazy {
        listOf(
            getString(R.string.privacy_public),
            getString(R.string.privacy_friends)
        )
    }

    // итераторы
    var iterator_privacy = 0
    private lateinit var app: MyApp
    private lateinit var user: DUsers
    private lateinit var pref: SharedPreferences
    private lateinit var contactsRepo: ContactsRepository
    private var myId: Int = 0
    private var otherId: Int = 0
    private var isContact = false
    private var isFriend = false
    private lateinit var btnAddContact: Button
    private lateinit var btnMakeFriend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)

        app = application as MyApp
        viewModel = ViewModelProvider(
            this,
            OtherTasksViewModelFactory(
                app.appContainer.otherTaskDao,
                app.appContainer.otherRepository
            )
        ).get(OtherTasksViewModel::class.java)
        contactsRepo = app.appContainer.contactsRepository

        pref = getSharedPreferences("settings", MODE_PRIVATE)
        myId = (application as MyApp).appContainer.tokenManager.getUserId()

        // Находим элементы
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val tvNickname = findViewById<TextView>(R.id.tv_friend_nickname)
        btnAddContact = findViewById(R.id.btn_add_contact)
        btnMakeFriend = findViewById(R.id.btn_make_friend)
        date_number = findViewById(R.id.other_date)
        day_week = findViewById(R.id.other_day_week)
        tasks_view = findViewById(R.id.other_tasks)
        privacy_button = findViewById(R.id.other_privacy)
        month_and_year = findViewById(R.id.other_month)
        avatar = findViewById(R.id.iv_friend_avatar)

        calendar = Calendar.getInstance()
        val id = intent.getIntExtra("other_id", 0)
        otherId = id

        lifecycleScope.launch {
            user = app.appContainer.userDao.getUserForId(id)
            tvNickname.text = user.username
            avatar.text = user.avatar

            viewModel.setId(user.id)

            viewModel.setDate(calendar.timeInMillis)
        }
        nextDayButton = findViewById(R.id.main_next_day)
        prevDayButton = findViewById(R.id.main_prev_day)

        nextDayButton.setOnClickListener {
            addDays(1)
        }

        prevDayButton.setOnClickListener {
            addDays(-1)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasksForDay.collect { tasks ->
                    tasks_view.setTasks2(tasks)
                }
            }
        }

        setDate()

        btnBack.setOnClickListener {
            finish()
        }

        btnAddContact.setOnClickListener {
            lifecycleScope.launch {
                try {
                    if (isContact) {
                        contactsRepo.removeContact(myId, otherId)
                    } else {
                        contactsRepo.addContact(myId, otherId)
                    }
                    refreshContactStatus()
                } catch (e: Exception) {
                    Log.d("error", e.message + "")
                    Toast.makeText(this@FriendProfileActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnMakeFriend.setOnClickListener {
            lifecycleScope.launch {
                try {
                    if (isFriend) {
                        contactsRepo.removeFriend(myId, otherId)
                    } else {
                        contactsRepo.addFriend(myId, otherId)
                    }
                    refreshContactStatus()
                } catch (e: Exception) {
                    Toast.makeText(this@FriendProfileActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            }
        }

        date_number.setOnClickListener {
            addDays(1)
        }

        date_number.setOnLongClickListener {
            addDays(-1)
            true
        }

        month_and_year.setOnClickListener {
            showMaterialDatePicker()
        }

        // Кнопка приватности
        updatePrivacyUI(false)

        privacy_button.setOnClickListener {
            // При клике переключаем (k = true)
            updatePrivacyUI(true)
        }

        // Кнопка дня недели
        day_week.setOnClickListener {
            if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
                addDays(-6)
            }
            else {
                addDays(1)
            }
        }

        day_week.setOnLongClickListener {
            if (calendar.get(Calendar.DAY_OF_WEEK) == 2) {
                addDays(6)
            }
            else {
                addDays(-1)
            }
            true
        }
    }

    private fun updatePrivacyUI(k: Boolean = true) {
        if (k) {

            iterator_privacy = (iterator_privacy + 1) % 2
            Toast.makeText(this, privacy_labels[iterator_privacy], Toast.LENGTH_SHORT).show()
        }

        // Меняем текст кнопки
        privacy_button.text = privacy_labels[iterator_privacy]


        privacy_button.setBackgroundColor(privacy_color[iterator_privacy].toInt())


        privacy_button.setIconResource(privacy_icons[iterator_privacy])


        viewModel.setPrivacy(privacy_text_ENUM[iterator_privacy])
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            refreshContactStatus()
        }
    }

    private suspend fun refreshContactStatus() {
        val contact = contactsRepo.getContactStatus(myId, otherId)
        isContact = contact != null
        isFriend = contact?.isFriend == true
        updateButtons()
    }

    private fun updateButtons() {
        btnAddContact.text = if (isContact) "Удалить из контактов" else "Добавить в контакты"
        // неудобно btnMakeFriend.visibility = if (isContact) View.VISIBLE else View.GONE
        btnMakeFriend.text = if (isFriend) "Удалить из друзей" else "Сделать другом"
    }

    private fun setDate() {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val weekday = calendar.get(Calendar.DAY_OF_WEEK)

        date_number.text = day.toString()
        month_and_year.text = "${monthes_text[month]} $year"
        day_week.text = week_text[(weekday + 5) % 7]
        viewModel.setDate(calendar.timeInMillis)
    }

    private fun addDays(delta: Int) {
        calendar.add(Calendar.DAY_OF_MONTH, delta)
        setDate()
    }

    private fun showMaterialDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату")               // Заголовок
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Текущая дата по умолчанию
            .build()

        // Устанавливаем слушатель нажатия кнопки "ОК"
        datePicker.addOnPositiveButtonClickListener { selection ->
            // selection — это выбранная дата в миллисекундах от начала эпохи (UTC)
            val calendar1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar1.timeInMillis = selection

            // Обновляем ваш календарь (переменную calendar)
            this.calendar.timeInMillis = selection
            // Обновляем текст на экране
            setDate()
            // Опционально: показываем Toast с выбранной датой
            Toast.makeText(this, "Выбрано: ${date_number.text}.${month_and_year.text}", Toast.LENGTH_SHORT).show()
        }

        // Показываем диалог
        datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER_TAG")
    }
}
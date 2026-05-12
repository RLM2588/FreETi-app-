package com.example.freeti

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.freeti.data.local.entity.DGroupEvents
import com.example.freeti.data.local.entity.DGroups
import com.example.freeti.view_model.GroupTasksViewModel
import com.example.freeti.view_model.GroupTasksViewModelFactory
import com.example.freeti.views.GroupTaskTimelineView
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var date_number: TextView
    private lateinit var day_week: TextView
    private lateinit var tasks_view: GroupTaskTimelineView
    private lateinit var month_and_year: TextView
    private lateinit var calendar: Calendar
    private lateinit var viewModel: GroupTasksViewModel

    private val week_text: List<String> = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    private val monthes_text: List<String> = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль",
        "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")

    private lateinit var app: MyApp
    private lateinit var pref: SharedPreferences

    private lateinit var toolbar: Toolbar
    private lateinit var textGroupName: TextView
    private lateinit var textDescription: TextView
    private lateinit var textMemberCount: TextView
    private lateinit var buttonCreateEvent: Button
    private lateinit var buttonrasp: Button
    private lateinit var group_members: Button
    private lateinit var tasks_lin: LinearLayout
    private lateinit var view_golosov: View // TODO потом заменим на нужное
    private lateinit var group: DGroups
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)

        app = application as MyApp
        viewModel = ViewModelProvider(
            this,
            GroupTasksViewModelFactory(
                app.appContainer.groupTasksDao,
                app.appContainer.groupTaskRepository
            )
        ).get(GroupTasksViewModel::class.java)

        pref = getSharedPreferences("settings", MODE_PRIVATE)

        toolbar = findViewById(R.id.toolbar)
        textGroupName = findViewById(R.id.textGroupName)
        textDescription = findViewById(R.id.textDescription)
        textMemberCount = findViewById(R.id.textMemberCount)
        buttonCreateEvent = findViewById(R.id.buttonCreateEvent)


        date_number = findViewById(R.id.group_date)
        day_week = findViewById(R.id.group_day_week)
        tasks_view = findViewById(R.id.group_tasks)
        month_and_year = findViewById(R.id.group_month)
        buttonrasp = findViewById(R.id.group_tasks_view)
        view_golosov = findViewById(R.id.group_golosov)
        tasks_lin = findViewById(R.id.group_tasks_lin)
        group_members = findViewById(R.id.group_members)

        calendar = Calendar.getInstance()
        id = intent.getStringExtra("group_id")?: "0"
        if(id == "0") finish()

        updateUi()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasksForDay.collect { tasks ->
                    tasks_view.setTasks2(tasks)
                }
            }
        }

        setDate()

        buttonCreateEvent.setOnClickListener {
            val intent = Intent(this, NewEventGroupActivity::class.java)
            intent.putExtra("groupId", group.id)
            intent.putExtra("daytime", calendar.timeInMillis)
            startActivity(intent)
        }

        tasks_view.onTaskClickListener = object : GroupTaskTimelineView.OnTaskClickListener {
            override fun onTaskClick(task: DGroupEvents) {
                lifecycleScope.launch {
                    if (app.appContainer.groupTaskRepository.deleteTask(task.id)){
                        showToast("Успешно")
                    }
                    else {
                        showToast("Ошибка подключения")
                    }
                }
            }
        }

        group_members.setOnClickListener {
            goOnMember()
        }

        buttonrasp.setOnClickListener {
            tasks_lin.visibility = View.VISIBLE
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
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

        textMemberCount.setOnClickListener {
            goOnMember()
        }

        textGroupName.setOnClickListener {
            goOnMember()
        }
    }

    fun goOnMember() {
        val intent = Intent(this@GroupDetailsActivity, MembersActivity::class.java)
        intent.putExtra("group_id", group.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    fun updateUi() {
        if(id == "0") return

        lifecycleScope.launch {
            group = app.appContainer.groupsDao.getGroup(id)
            textGroupName.text = group.title
            textDescription.text = group.body
            textMemberCount.text = app.appContainer.groupMemberDao.getCountGroupMembers(id).toString()

            viewModel.setId(group.id)

            if(!pref.getBoolean("noTestAdd", false)) {
                viewModel.addTestTasks()
            }

            viewModel.setDate(calendar.timeInMillis)
        }
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
            .setTitleText("Выберите дату")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar1.timeInMillis = selection
            this.calendar.timeInMillis = selection
            setDate()
            Toast.makeText(this, "Выбрано: ${date_number.text}.${month_and_year.text}", Toast.LENGTH_SHORT).show()
        }

        // Показываем диалог
        datePicker.show(supportFragmentManager, "MATERIAL_DATE_PICKER_TAG")
    }

    private fun showToast(message: String) {
        Toast.makeText(this@GroupDetailsActivity, message, Toast.LENGTH_SHORT).show()
    }
}
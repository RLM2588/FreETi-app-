package com.example.freeti.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeti.data.local.entity.DOtherTasks
import com.example.freeti.data_base.OtherTasksDao
import com.example.freeti.repository.OtherTaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class OtherTasksViewModel(
    private val taskDao: OtherTasksDao,
    private val rep: OtherTaskRepository
) : ViewModel() {
    private val _selectedDateMillis = MutableStateFlow<Long?>(null)
    private var user_id: Int = 0

    private val _privacy = MutableStateFlow("PUBLIC")
    val privacy: StateFlow<String> = _privacy.asStateFlow()

    val tasksForDay: StateFlow<List<DOtherTasks>> = combine(
        _selectedDateMillis,
        _privacy
    ) { dateMillis, privacy ->
        dateMillis to privacy
    }.flatMapLatest { (dateMillis, privacy) ->
        if (dateMillis == null) {
            flowOf(emptyList())
        } else {
            val start = getStartOfLocalDayUtc(dateMillis)
            val end = getStartOfNextLocalDayUtc(dateMillis)  // начало следующего дня
            taskDao.getTasksForDateRange(start, end, privacy, user_id)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setDate(millis: Long) {
        if (_selectedDateMillis.value == millis) return
        _selectedDateMillis.value = millis

        viewModelScope.launch {
            if (user_id <= 0) return@launch
            val yearMonth = convertMillisToYearMonth(millis)
            rep.getTasks(yearMonth, user_id)
        }
    }

    fun setPrivacy(privacy: String) {
        if (_privacy.value == privacy) return
        _privacy.value = privacy
    }

    fun setId(id: Int) {
        user_id = id
    }

    private fun convertMillisToYearMonth(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }

    fun getStartOfLocalDayUtc(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getStartOfNextLocalDayUtc(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_MONTH, 1)
        return cal.timeInMillis
    }

    fun addTestTasks() {
        viewModelScope.launch {
            // Убедимся, что у нас есть начало сегодняшнего дня (UTC)
            val todayStart = getStartOfLocalDayUtc(System.currentTimeMillis())
            val testTasks = listOf(
                DOtherTasks(
                    id = "test_1",
                    title = "Утренняя пробежка",
                    body = "Бег в парке",
                    user_id = user_id,
                    start = todayStart + 7 * 3600_000L,          // 07:00
                    time_end = todayStart + 8 * 3600_000L,        // 08:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 2,
                    push_template_id = 0,
                    colour = "FF5733"
                ),
                DOtherTasks(
                    id = "test_2",
                    title = "Встреча с командой",
                    body = "Обсуждение спринта",
                    user_id = user_id,
                    start = todayStart + 7 * 3600_000L + 30 * 60_000L, // 07:30
                    time_end = todayStart + 9 * 3600_000L,              // 09:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 3,
                    push_template_id = 0,
                    colour = "33FF57"
                ),
                DOtherTasks(
                    id = "test_9",
                    title = "Встреча с командой2",
                    body = "Обсуждение спринта",
                    user_id = user_id,
                    start = todayStart + 7 * 3600_000L + 30 * 60_000L, // 07:30
                    time_end = todayStart + 9 * 3600_000L,              // 09:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 3,
                    push_template_id = 0,
                    colour = "33F457"
                ),
                DOtherTasks(
                    id = "test_91",
                    title = "Встреча с командой23",
                    body = "Обсуждение спринта",
                    user_id = user_id,
                    start = todayStart + 7 * 3600_000L + 30 * 60_000L, // 07:30
                    time_end = todayStart + 9 * 3600_000L,              // 09:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 3,
                    push_template_id = 0,
                    colour = "53F457"
                ),
                DOtherTasks(
                    id = "test_3",
                    title = "Завтрак",
                    body = "Сходить в кафе",
                    user_id = user_id,
                    start = todayStart + 8 * 3600_000L + 15 * 60_000L, // 08:15
                    time_end = todayStart + 9 * 3600_000L,              // 09:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 1,
                    push_template_id = 0,
                    colour = "3357FF"
                ),
                DOtherTasks(
                    id = "test_4",
                    title = "Поздний дедлайн",
                    body = "Сдать отчёт",
                    user_id = user_id,
                    start = todayStart + 16 * 3600_000L,                // 16:00
                    time_end = todayStart + 18 * 3600_000L,              // 18:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 3,
                    push_template_id = 0,
                    colour = "FF33A1"
                ),
                DOtherTasks(
                    id = "test_8",
                    title = "Поздний дедлайн2",
                    body = "Сдать отчёт",
                    user_id = user_id,
                    start = todayStart + 28 * 3600_000L,                // 16:00
                    time_end = todayStart + 31 * 3600_000L,              // 18:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 3,
                    push_template_id = 0,
                    colour = "FFF3A1"
                )
            )
            taskDao.insertAll(testTasks)
        }
    }
}
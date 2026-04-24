package com.example.freeti.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeti.data.local.entity.DTasks
import com.example.freeti.data_base.MyTasksDao
import com.example.freeti.sync.TaskSyncManager
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
import java.util.TimeZone

class TasksViewModel(
    private val taskDao: MyTasksDao,
    private val syncManager: TaskSyncManager
) : ViewModel() {
    private val _selectedDateMillis = MutableStateFlow<Long?>(null)
    val selectedDateMillis: StateFlow<Long?> = _selectedDateMillis.asStateFlow()

    private val _privacy = MutableStateFlow("PUBLIC")
    val privacy: StateFlow<String> = _privacy.asStateFlow()

    val tasksForDay: StateFlow<List<DTasks>> = combine(
        _selectedDateMillis,
        _privacy
    ) { dateMillis, privacy ->
        dateMillis to privacy
    }.flatMapLatest { (dateMillis, privacy) ->
        if (dateMillis == null) {
            flowOf(emptyList())
        } else {
            val start = getStartOfDayUtc(dateMillis)
            val end = getStartOfNextDayUtc(dateMillis)  // начало следующего дня
            taskDao.getTasksForDateRange(start, end, privacy)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setDate(millis: Long) {
        if (_selectedDateMillis.value == millis) return
        _selectedDateMillis.value = millis

        val yearMonth = convertMillisToYearMonth(millis)
        viewModelScope.launch {
            syncManager.syncMonth(yearMonth)
        }
    }

    fun setPrivacy(privacy: String) {
        if (_privacy.value == privacy) return
        _privacy.value = privacy
    }

    fun forceRefresh() {
        val currentDateMillis = _selectedDateMillis.value ?: return
        val yearMonth = convertMillisToYearMonth(currentDateMillis)
        viewModelScope.launch {
            syncManager.syncMonth(yearMonth, force = true)
        }
    }

    private fun convertMillisToYearMonth(millis: Long): String {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = millis

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        return String.format(Locale.US, "%04d-%02d", year, month)
    }

    fun getStartOfDayUtc(millis: Long): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = millis

        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
    fun getStartOfNextDayUtc(millis: Long): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_MONTH, 1)
        return cal.timeInMillis
    }

    // в TasksViewModel.kt
    fun addTestTasks() {
        viewModelScope.launch {
            // Убедимся, что у нас есть начало сегодняшнего дня (UTC)
            val todayStart = getStartOfDayUtc(System.currentTimeMillis())
            val testTasks = listOf(
                DTasks(
                    id = "test_1",
                    title = "Утренняя пробежка",
                    body = "Бег в парке",
                    start = todayStart + 7 * 3600_000L,          // 07:00
                    time_end = todayStart + 8 * 3600_000L,        // 08:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 2,
                    push_template_id = 0,
                    colour = "FF5733",          // оранжевый
                    updated_at = System.currentTimeMillis(),
                    is_delete = false
                ),
                DTasks(
                    id = "test_2",
                    title = "Встреча с командой",
                    body = "Обсуждение спринта",
                    start = todayStart + 7 * 3600_000L + 30 * 60_000L, // 07:30
                    time_end = todayStart + 9 * 3600_000L,              // 09:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 3,
                    push_template_id = 0,
                    colour = "33FF57",          // зелёный
                    updated_at = System.currentTimeMillis(),
                    is_delete = false
                ),
                DTasks(
                    id = "test_9",
                    title = "Встреча с командой2",
                    body = "Обсуждение спринта",
                    start = todayStart + 7 * 3600_000L + 30 * 60_000L, // 07:30
                    time_end = todayStart + 9 * 3600_000L,              // 09:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 3,
                    push_template_id = 0,
                    colour = "33F457",          // зелёный
                    updated_at = System.currentTimeMillis(),
                    is_delete = false
                ),
                DTasks(
                    id = "test_91",
                    title = "Встреча с командой23",
                    body = "Обсуждение спринта",
                    start = todayStart + 7 * 3600_000L + 30 * 60_000L, // 07:30
                    time_end = todayStart + 9 * 3600_000L,              // 09:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 3,
                    push_template_id = 0,
                    colour = "53F457",          // зелёный
                    updated_at = System.currentTimeMillis(),
                    is_delete = false
                ),
                DTasks(
                    id = "test_3",
                    title = "Завтрак",
                    body = "Сходить в кафе",
                    start = todayStart + 8 * 3600_000L + 15 * 60_000L, // 08:15
                    time_end = todayStart + 9 * 3600_000L,              // 09:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 1,
                    push_template_id = 0,
                    colour = "3357FF",          // синий
                    updated_at = System.currentTimeMillis(),
                    is_delete = false
                ),
                DTasks(
                    id = "test_4",
                    title = "Поздний дедлайн",
                    body = "Сдать отчёт",
                    start = todayStart + 16 * 3600_000L,                // 16:00
                    time_end = todayStart + 18 * 3600_000L,              // 18:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 3,
                    push_template_id = 0,
                    colour = "FF33A1",          // розовый
                    updated_at = System.currentTimeMillis(),
                    is_delete = false
                ),
                DTasks(
                    id = "test_8",
                    title = "Поздний дедлайн2",
                    body = "Сдать отчёт",
                    start = todayStart + 20 * 3600_000L,                // 16:00
                    time_end = todayStart + 21 * 3600_000L,              // 18:00
                    status = "ACTIVE",
                    privacy = "PUBLIC",
                    importance = 3,
                    push_template_id = 0,
                    colour = "FFF3A1",          // розовый
                    updated_at = System.currentTimeMillis(),
                    is_delete = false
                )
            )
            taskDao.insertAll(testTasks)
        }
    }
}
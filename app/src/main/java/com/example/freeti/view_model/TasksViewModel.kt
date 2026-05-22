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

class TasksViewModel(
    private val taskDao: MyTasksDao,
    private val syncManager: TaskSyncManager
) : ViewModel() {
    private val _selectedDateMillis = MutableStateFlow<Long?>(null)
    //val selectedDateMillis: StateFlow<Long?> = _selectedDateMillis.asStateFlow()

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
            val start = getStartOfLocalDayUtc(dateMillis)
            val end = getStartOfNextLocalDayUtc(dateMillis)  // начало следующего дня
            taskDao.getTasksForDateRange(start, end, privacy)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasksWithoutTime: StateFlow<List<DTasks>> = combine(
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

            taskDao.observeTasksWithoutTime(start, end)
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
            syncManager.syncPendingTasks()
            syncManager.syncMonth(yearMonth, force = true)
        }
    }

    private fun convertMillisToYearMonth(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1

        return String.format(Locale.US, "%04d-%02d", year, month)
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

    fun markTaskDone(task: DTasks) {
        viewModelScope.launch {
            val newStatus = if (task.status == "ACTIVE") "DONE" else "ACTIVE"   // переключаем
            val updatedTask = task.copy(
                status = newStatus,
                updated_at = System.currentTimeMillis(),
                is_synced = false
            )
            taskDao.insertAll(listOf(updatedTask))
        }
    }

    fun moveTaskToNextDay(task: DTasks) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            cal.timeInMillis = task.start
            cal.add(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val newStart = cal.timeInMillis
            val updatedTask = task.copy(
                start = newStart,
                updated_at = System.currentTimeMillis(),
                is_synced = false
            )
            taskDao.insertAll(listOf(updatedTask))
        }
    }
}
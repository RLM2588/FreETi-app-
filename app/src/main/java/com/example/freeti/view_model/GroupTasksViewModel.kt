package com.example.freeti.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeti.data.local.entity.DGroupEvents
import com.example.freeti.data_base.GroupTasksDao
import com.example.freeti.repository.GroupTaskRepository
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

class GroupTasksViewModel(
    private val taskDao: GroupTasksDao,
    private val rep: GroupTaskRepository
) : ViewModel() {
    private val _selectedDateMillis = MutableStateFlow<Long?>(null)
    private var user_id = MutableStateFlow("0")
    val id: StateFlow<String> = user_id.asStateFlow()


    val tasksForDay: StateFlow<List<DGroupEvents>> = combine(
        _selectedDateMillis,
        user_id
    ) { dateMillis, id->
        dateMillis to id
    }.flatMapLatest { (dateMillis, id) ->
        if (dateMillis == null) {
            flowOf(emptyList())
        } else {
            val start = getStartOfLocalDayUtc(dateMillis)
            val end = getStartOfNextLocalDayUtc(dateMillis)  // начало следующего дня
            taskDao.getTasksForDateRange(start, end, id)

        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setDate(millis: Long) {
        if (_selectedDateMillis.value == millis) return

        viewModelScope.launch {
            if (user_id.value == "0") return@launch
            val yearMonth = convertMillisToYearMonth(millis)
            rep.getTasks(yearMonth, user_id.value)

            _selectedDateMillis.value = millis
        }
    }

    fun setId(id: String) {
        user_id.value = id
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
}
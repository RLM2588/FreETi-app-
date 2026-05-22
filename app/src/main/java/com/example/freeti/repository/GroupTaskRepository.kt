package com.example.freeti.repository

import android.util.Log
import com.example.freeti.data_base.GroupTasksDao
import com.example.freeti.network_api.ApiService
import com.example.freeti.network_entity.NGroupEventSearch
import java.util.Calendar
import java.util.Locale
import kotlin.collections.map

class GroupTaskRepository(
    private val api: ApiService,
    private val dao: GroupTasksDao
) {
    private fun convertMillisToYearMonth(millis: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }
    suspend fun getTasks(yearMonth: String, group_id: String): Boolean {
        return try {
            val response = api.getGroupTasksForDay(yearMonth, group_id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val dTasks = body.map { it.toEntity() }
                    if (dTasks.isNotEmpty()) {
                        dao.deleteTasksForMonth(yearMonth, group_id)
                        dao.insertAll(dTasks)
                        val res = dao.observeAllTasks(group_id)
                        res.map { Log.d("dao", convertMillisToYearMonth(it.start) + " " + convertMillisToYearMonth(it.time_end)) }
                    }
                } else {
                    dao.deleteTasksForMonth(yearMonth, group_id)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun sendTasks(group_id: String, event: NGroupEventSearch): Boolean {
        return try {
            val response = api.addEvent(group_id, event)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val dTasks = body.map { it.toEntity() }
                    Log.d("log", "dtasks is not empty?: " + dTasks.isNotEmpty())
                    if (dTasks.isNotEmpty()) {
                        dao.insertAll(dTasks)
                        true
                    } else false
                } else { false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.d("error", e.message + "")
            false
        }
    }

    suspend fun deleteTask(task_id: String): Boolean {
        return try {
            val response = api.deleteGroupEvent(task_id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body == true) {
                    dao.deleteTask(task_id)
                    true
                }
                else false
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
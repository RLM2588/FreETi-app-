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
            Log.d("pre_n", "niggaaaaaaaaaaaaaaaaaa")
            val response = api.getGroupTasksForDay(yearMonth, group_id)
            Log.d("full_n",response.isSuccessful.toString() + " " + yearMonth + " " +
                    group_id + " " + (response.body() != null).toString())
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("take_n",body.size.toString() + " " + yearMonth + " " + group_id)
                    val dTasks = body.map { Log.d("group task", convertMillisToYearMonth(it.start) + " " + convertMillisToYearMonth(it.time_end)); it.toEntity() }

                    Log.d("take",dTasks.size.toString())
                    if (dTasks.isNotEmpty()) {
                        dao.deleteTasksForMonth(yearMonth, group_id)
                        dao.insertAll(dTasks)
                        val res = dao.observeAllTasks(group_id)
                        res.map { Log.d("dao", convertMillisToYearMonth(it.start) + " " + convertMillisToYearMonth(it.time_end)) }
                    }
                } else {
                    dao.deleteTasksForMonth(yearMonth, group_id)
                }
                Log.d("after_take",dao.count(group_id).toString())
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
                    if (dTasks.isNotEmpty()) {
                        dao.insertAll(dTasks)
                        Log.d("response", "ok")
                        true
                    } else false
                } else { false
                }
            } else {
                false
            }
        } catch (e: Exception) {
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
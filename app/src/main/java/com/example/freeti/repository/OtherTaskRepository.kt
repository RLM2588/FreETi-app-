package com.example.freeti.repository

import com.example.freeti.data_base.OtherTasksDao
import com.example.freeti.network_api.ApiService

class OtherTaskRepository(
    private val api: ApiService,
    private val dao: OtherTasksDao
) {
    suspend fun getTasks(yearMonth: String, user_id: Int): Boolean {
        return try {
            val response = api.getOtherTasksForDay(yearMonth, user_id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val dTasks = body.map { it.toEntity() }
                    if (dTasks.isNotEmpty()) {
                        dao.deleteTasksForMonth(yearMonth, user_id)
                        dao.insertAll(dTasks)
                    }
                } else {
                    // Пустой ответ – просто удаляем старые задачи за этот день
                    //dao.deleteTasksForMonth(yearMonth, user_id)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
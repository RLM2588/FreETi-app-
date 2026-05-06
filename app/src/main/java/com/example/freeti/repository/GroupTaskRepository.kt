package com.example.freeti.repository

import com.example.freeti.data_base.GroupTasksDao
import com.example.freeti.network_api.ApiService
import kotlin.collections.map

class GroupTaskRepository(
    private val api: ApiService,
    private val dao: GroupTasksDao
) {
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
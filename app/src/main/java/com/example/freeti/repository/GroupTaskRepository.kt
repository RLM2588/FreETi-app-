package com.example.freeti.repository

import com.example.freeti.data_base.GroupTasksDao
import com.example.freeti.network_api.ApiService
import com.example.freeti.network_entity.NGroupEventSearch
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

    suspend fun sendTasks(group_id: String, event: NGroupEventSearch): Boolean {
        return try {
            val response = api.addEvent(group_id, event)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val dTasks = body.map { it.toEntity() }
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
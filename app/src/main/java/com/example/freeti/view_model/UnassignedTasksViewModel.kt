package com.example.freeti.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freeti.data.local.entity.DTasks
import com.example.freeti.data_base.MyTasksDao
import com.example.freeti.network_api.ApiService
import com.example.freeti.sync.TaskSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UnassignedTasksViewModel(
    private val taskDao: MyTasksDao,
    private val syncManager: TaskSyncManager,
    private val apiService: ApiService
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<DTasks>>(emptyList())
    val tasks: StateFlow<List<DTasks>> = _tasks.asStateFlow()

    init {
        viewModelScope.launch {
            taskDao.observeUnassignedTasks().collect {
                _tasks.value = it
            }
        }
        syncWithServer()
    }

    fun syncWithServer() {
        viewModelScope.launch {
            try {
                syncManager.syncPendingTasks()
                val remoteTasks = apiService.getUnassignedTasks()
                taskDao.deleteAllUnassignedTasks()
                taskDao.insertAll(remoteTasks.map { it.toUnassignedTask() })
            } catch (e: Exception) {
            }
        }
    }
}
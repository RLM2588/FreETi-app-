package com.example.freeti.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.freeti.data_base.MyTasksDao
import com.example.freeti.network_api.ApiService
import com.example.freeti.sync.TaskSyncManager

class UnassignedTasksViewModelFactory(
    private val taskDao: MyTasksDao,
    private val syncManager: TaskSyncManager,
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnassignedTasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UnassignedTasksViewModel(taskDao, syncManager, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
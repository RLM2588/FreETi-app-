package com.example.freeti.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.freeti.data_base.MyTasksDao
import com.example.freeti.sync.TaskSyncManager

class TasksViewModelFactory(
    private val myTaskDao: MyTasksDao,
    private val syncManager: TaskSyncManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(myTaskDao, syncManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
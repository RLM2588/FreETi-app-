package com.example.freeti.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.freeti.data_base.GroupTasksDao
import com.example.freeti.repository.GroupTaskRepository

class GroupTasksViewModelFactory(
    private val myTaskDao: GroupTasksDao,
    private val rep: GroupTaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupTasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupTasksViewModel(myTaskDao, rep) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.freeti.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.freeti.data_base.OtherTasksDao
import com.example.freeti.repository.OtherTaskRepository

class OtherTasksViewModelFactory(
    private val myTaskDao: OtherTasksDao,
    private val rep: OtherTaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtherTasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OtherTasksViewModel(myTaskDao, rep) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
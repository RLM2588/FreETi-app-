package com.example.freeti.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.freeti.data.local.entity.DUsers
import com.example.freeti.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<DUsers>>(emptyList())
    val users: StateFlow<List<DUsers>> = _users.asStateFlow()

    init {
        viewModelScope.launch {
            repository.prepareLocalData()
            _users.value = repository.getAllUsers()
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _users.value = repository.search(query)
        }
    }
}

class SearchViewModelFactory(
    private val repository: SearchRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.example.freeti.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.freeti.data_base.UserWithContactStatus
import com.example.freeti.repository.ContactsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: ContactsRepository) : ViewModel() {
    private val _contacts = MutableStateFlow<List<UserWithContactStatus>>(emptyList())
    val contacts: StateFlow<List<UserWithContactStatus>> = _contacts

    init { loadContacts() }

    fun loadContacts() {
        viewModelScope.launch {
            // сначала синхронизируемся с сервером
            repository.syncContacts()
            _contacts.value = repository.getContacts()
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _contacts.value = repository.searchContacts(query)
        }
    }
}

class ContactsViewModelFactory(
    private val repository: ContactsRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
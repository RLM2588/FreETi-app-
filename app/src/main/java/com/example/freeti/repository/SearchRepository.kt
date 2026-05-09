package com.example.freeti.repository

import com.example.freeti.data.local.entity.DUsers
import com.example.freeti.data_base.UserDao
import com.example.freeti.network_api.ApiService
import com.example.freeti.tokens.TokenManager

class SearchRepository(
    private val userDao: UserDao,
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun prepareLocalData() {
        val myId = tokenManager.getUserId()// ?: throw IllegalStateException("User id not found")

        userDao.deleteOtherUsers(myId)

        val existing = userDao.getAllExcept(myId)
        if (existing.isEmpty()) {
            val testUsers = listOf(
                DUsers(id = 101, login = "john_doe", username = "John Doe", avatar = ":3"),
                DUsers(id = 102, login = "jane_smith", username = "Jane Smith", avatar = ">:)"),
                DUsers(id = 103, login = "happy_cat", username = "Happy Cat", avatar = ":0")
            )
            userDao.insertAll(testUsers)
        } // TODO для тестов
    }

    suspend fun getAllUsers(): List<DUsers> {
        val myId = tokenManager.getUserId()// ?: throw IllegalStateException("User id not found")
        return userDao.getAllExcept(myId)
    }

    suspend fun search(query: String): List<DUsers> {
        val myId = tokenManager.getUserId() ?: throw IllegalStateException("User id not found")
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return userDao.getAllExcept(myId)
        }

        val isLoginSearch = trimmed.startsWith("@")
        val searchQuery = if (isLoginSearch) trimmed.substring(1).trim() else trimmed

        try {
            val networkUsers = if (isLoginSearch) {
                apiService.getUsersSearchByLogin(searchQuery)
            } else {
                apiService.getUsersSearch(searchQuery)
            }
            val dUsers = networkUsers.map { it.toEntity() }
            userDao.insertAll(dUsers)
        } catch (e: Exception) {
        }

        // Локальный фильтр
        return if (isLoginSearch) {
            userDao.searchByLogin(myId, searchQuery)
        } else {
            userDao.searchByUsername(myId, searchQuery)
        }
    }
}
package com.example.freeti.repository

import com.example.freeti.data.local.entity.DContacts
import com.example.freeti.data_base.ContactsDao
import com.example.freeti.data_base.UserDao
import com.example.freeti.data_base.UserWithContactStatus
import com.example.freeti.network_api.ApiService
import com.example.freeti.network_entity.NContacts
import com.example.freeti.tokens.TokenManager

class ContactsRepository(
    private val contactsDao: ContactsDao,
    private val userDao: UserDao,
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun syncContacts() {
        val myId = tokenManager.getUserId()
        try {
            val networkContacts = apiService.getContacts()
            if (networkContacts.isSuccessful && networkContacts.body() != null) {
                val dContacts = networkContacts.body()?.map { it.toEntity() }
                if (dContacts == null) throw NoSuchMethodException()

                contactsDao.deleteAllContacts(myId)
                contactsDao.insertAll(dContacts)

                val allIds = dContacts.flatMap { listOf(it.user1, it.user2) }.distinct()
                val existingIds =
                    userDao.getAllExcept(myId).map { it.id }.toSet() // 0 шутка, лучше getAll
                val missingIds = allIds.filter { it !in existingIds && it != myId }
                if (missingIds.isNotEmpty()) {
                    val users = apiService.getUsersByIds(missingIds.joinToString(","))
                    val body = users.body();
                    if (users.isSuccessful && body != null) {
                        userDao.insertAll(body.map { it.toEntity() })
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    suspend fun getContacts(): List<UserWithContactStatus> {
        val myId = tokenManager.getUserId()
        return contactsDao.getContacts(myId)
    }

    suspend fun searchContacts(query: String): List<UserWithContactStatus> {
        val myId = tokenManager.getUserId()
        if (query.isBlank()) return getContacts()
        return contactsDao.searchContacts(myId, query.trim())
    }

    suspend fun addContact(myId: Int, otherId: Int) {
        val contact = NContacts(user1 = myId, user2 = otherId, isFriend = false)
        val response = apiService.upsertContact(contact)
        if (response.isSuccessful) {
            val dContact = DContacts(myId, otherId, isFriend = false)
            contactsDao.insertOrUpdate(dContact)
        } else {
            throw Exception("Server error: ${response.code()}")
        }
    }

    // Удалить контакт полностью (вместе с дружбой, если была)
    suspend fun removeContact(myId: Int, otherId: Int) {
        val contact =
            NContacts(user1 = myId, user2 = otherId, isFriend = false) // серверу всё равно
        val response = apiService.deleteContact(myId, otherId)
        if (response.isSuccessful) {
            // Удаляем локально, если запись существует
            val dContact = DContacts(myId, otherId, isFriend = false)
            contactsDao.delete(dContact) // Room сам найдёт по primaryKey
        }
    }

    // Повысить до друга (isFriend = true)
    suspend fun addFriend(myId: Int, otherId: Int) {
        val contact = NContacts(myId, otherId, isFriend = true)
        val response = apiService.upsertContact(contact)
        if (response.isSuccessful) {
            contactsDao.insertOrUpdate(DContacts(myId, otherId, isFriend = true))
        }
    }

    // Убрать из друзей, оставаясь контактом
    suspend fun removeFriend(myId: Int, otherId: Int) {
        // Отправляем обновление с isFriend = false
        val contact = NContacts(myId, otherId, isFriend = false)
        val response = apiService.upsertContact(contact)
        if (response.isSuccessful) {
            contactsDao.insertOrUpdate(DContacts(myId, otherId, isFriend = false))
        }
    }

    // Получить текущий статус
    suspend fun getContactStatus(myId: Int, otherId: Int): DContacts? {
        return contactsDao.getContactStatus(myId, otherId)
    }
}
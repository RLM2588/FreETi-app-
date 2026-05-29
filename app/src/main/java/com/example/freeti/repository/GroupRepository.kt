package com.example.freeti.repository

import android.util.Log
import com.example.freeti.data.local.entity.DGroups
import com.example.freeti.data.local.entity.DGroupsUsers
import com.example.freeti.data_base.GroupMembersDao
import com.example.freeti.data_base.GroupsDao
import com.example.freeti.network_api.ApiService
import com.example.freeti.tokens.TokenManager
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GroupRepository(
    private val groupsDao: GroupsDao,
    private val apiService: ApiService
) {
    val allGroups: Flow<List<DGroups>> = groupsDao.getAllGroupsFlow()

    // отправка несинхронизированных + загрузка с сервера
    suspend fun syncGroups() {
        pushUnsyncedGroups()
        try {
            val serverGroups = apiService.getGroups()
            val entities = serverGroups.map { it.toEntity() }
            groupsDao.deleteAll() // Удаляем если получаем с сервера
            groupsDao.insertAll(entities)
        } catch (e: Exception) {
            Log.w("GroupRepo", "Failed to fetch groups from server", e)
        }
    }

    private suspend fun pushUnsyncedGroups() {
        val unsynced = groupsDao.getUnsyncedGroups()

        for (localGroup in unsynced) {
            try {
                val networkGroup = localGroup.toNetworkEntity()
                val response = apiService.createGroup(networkGroup)
                if (response.isSuccessful) {
                    val serverGroup = response.body()!!
                    groupsDao.markAsSynced(localGroup.id, serverGroup.id)
                } else {
                    Log.w("GroupRepo", "Sync failed for group ${localGroup.id}: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.w("GroupRepo", "Network error syncing group ${localGroup.id}", e)
            }
        }
    }

    suspend fun addGroup(title: String, body: String): DGroups {
        val localGroup = DGroups(
            id = UUID.randomUUID().toString(),
            title = title,
            body = body,
            isSynced = false
        )
        groupsDao.insertGroup(localGroup)
        pushUnsyncedGroups()

        return localGroup
    }

    suspend fun addGroupWithBody(title: String, body: String): DGroups {
        val localGroup = DGroups(
            id = UUID.randomUUID().toString(),
            title = title,
            body = body,
            isSynced = false
        )
        groupsDao.insertGroup(localGroup)
        pushUnsyncedGroups()

        return localGroup
    }
}
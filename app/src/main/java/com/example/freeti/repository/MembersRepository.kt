package com.example.freeti.repository

import android.util.Log
import com.example.freeti.data_base.GroupMembersDao
import com.example.freeti.data_base.UserDao
import com.example.freeti.network_api.ApiService
import org.jetbrains.annotations.Async
import kotlin.collections.map

class MembersRepository (
    private val api: ApiService,
    private val dao: GroupMembersDao,
    private val usersDao: UserDao
) {
    suspend fun getMembers(group_id: String): Boolean {
        return try {
            val response = api.getGroupMembers(group_id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val dmembers = body.map { it.toEntity() }
                    if (dmembers.isNotEmpty()) {
                        dao.deleteGroupMembersId(group_id)
                        dao.insertAll(dmembers)
                    }
                }
            } else {
                return false
            }

            val response1 = api.getGroupUsers(group_id)
            if (response1.isSuccessful) {
                val body = response1.body()
                if (body != null) {
                    val dusers = body.map { it.toEntity() }
                    if (dusers.isNotEmpty()) {
                        usersDao.insertAll(dusers)
                        Log.d("take_users", dusers.size.toString())
                    }
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCountMembers(group_id: String): Int {
        return try {
            val response = api.getCountUsersInGroup(group_id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    body
                } else { 0 }
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getRoleById(groupId: String, userId: Int): String {
        return try {
            dao.getRoleById(groupId, userId)
        } catch (e: Exception) {
            Log.d("members", e.toString())
            "OWNER"
        }
    }

    suspend fun deleteMember(group_id: String, user_id: Int): Boolean {
        return try {
            val response = api.deleteMember(user_id, group_id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body == true) {
                    dao.deleteGroupMember(group_id, user_id)
                    usersDao.deleteUser(user_id)
                    true
                }
                else false
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun roleMember(group_id: String, user_id: Int): Boolean {
        return try {
            val response = api.switchMember(user_id, group_id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val dmembers = body.toEntity()
                    dao.insertOne(dmembers)
                    true
                }
                else false
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteGroup(groupId: String): Boolean {
        return try {
            val response = api.deleteGroup(groupId)
            if (response.isSuccessful && response.body() == true) {
                // Если сервер успешно удалил группу, вычищаем её из локальной БД
                dao.deleteGroupMembersId(groupId)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun leaveGroup(groupId: String): Boolean {
        return try {
            val response = api.leaveGroup(groupId)
            if (response.isSuccessful && response.body() == true) {
                // После выхода просто удаляем кэш группы
                dao.deleteGroupMembersId(groupId)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
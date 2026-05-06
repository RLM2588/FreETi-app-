package com.example.freeti.repository

import com.example.freeti.data_base.GroupMembersDao
import com.example.freeti.data_base.UserDao
import com.example.freeti.network_api.ApiService
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
}
package com.example.freeti.data_base

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.freeti.Member
import com.example.freeti.data.local.entity.DGroupsUsers

@Dao
interface GroupMembersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DGroupsUsers>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(tasks: DGroupsUsers)

    @Query("SELECT * FROM groups_users WHERE tgroup = :groupId")
    suspend fun getGroupMembers(groupId: String): List<DGroupsUsers>

    @Query("SELECT users.id, users.username, users.avatar, groups_users.role FROM users JOIN groups_users WHERE groups_users.tgroup = :groupId AND users.id = groups_users.user1")
    suspend fun getGroupMembersMemb(groupId: String): List<Member>

    @Query("SELECT COUNT(*) FROM groups_users WHERE tgroup = :groupId")
    suspend fun getCountGroupMembers(groupId: String): Int

    @Query("DELETE FROM groups_users")
    suspend fun deleteGroupMembers()

    @Query("DELETE FROM groups_users WHERE tgroup = :groupId AND user1 = :userId")
    suspend fun deleteGroupMember(groupId: String, userId: Int)

    @Query("DELETE FROM groups_users WHERE tgroup = :groupId")
    suspend fun deleteGroupMembersId(groupId: String)
}
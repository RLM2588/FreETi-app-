package com.example.freeti.data_base

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.freeti.data.local.entity.DGroupsUsers

@Dao
interface GroupMembersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DGroupsUsers>)

    @Query("SELECT * FROM groups_users WHERE tgroup = :groupId")
    suspend fun getGroupMembers(groupId: String): List<DGroupsUsers>

    @Query("SELECT COUNT(*) FROM groups_users WHERE tgroup = :groupId")
    suspend fun getCountGroupMembers(groupId: String): Int

    @Query("DELETE FROM groups_users")
    suspend fun deleteGroupMembers()
}
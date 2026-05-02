package com.example.freeti.data_base

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.freeti.data.local.entity.DUsers

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DUsers>)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserForId(id: Int): DUsers

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUser(username: String): DUsers

    @Query("SELECT * FROM users WHERE id != :id")
    suspend fun getUsers(id: Int): List<DUsers>

    @Query("DELETE FROM users WHERE id != :id")
    suspend fun deleteOtherUsers(id: Int)
}
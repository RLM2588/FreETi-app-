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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: DUsers)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserForId(id: Int): DUsers

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUser(username: String): DUsers

    //@Query("SELECT * FROM users WHERE id != :id")
    //suspend fun getUsers(id: Int): List<DUsers> // повторение логики?

    // Получить всех, кроме себя
    @Query("SELECT * FROM users WHERE id != :excludeId ORDER BY username")
    suspend fun getAllExcept(excludeId: Int): List<DUsers>

    // Локальный поиск по нику (подстрока)
    @Query("SELECT * FROM users WHERE id != :excludeId AND username LIKE '%' || :query || '%' ORDER BY username")
    suspend fun searchByUsername(excludeId: Int, query: String): List<DUsers>

    // Локальный поиск по логину (подстрока)
    @Query("SELECT * FROM users WHERE id != :excludeId AND login LIKE '%' || :query || '%' ORDER BY username")
    suspend fun searchByLogin(excludeId: Int, query: String): List<DUsers>

    @Query("DELETE FROM users WHERE id != :id")
    suspend fun deleteOtherUsers(id: Int)
}
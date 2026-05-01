package com.example.freeti.data_base

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.freeti.data.local.entity.DGroups
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DGroups>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: DGroups)

    // Наблюдаемый список всех групп
    @Query("SELECT * FROM tgroups WHERE is_deleted = 0 ORDER BY title")
    fun getAllGroupsFlow(): Flow<List<DGroups>>

    // Несинхронизированные группы
    @Query("SELECT * FROM tgroups WHERE is_deleted = 0 AND isSynced = 0")
    suspend fun getUnsyncedGroups(): List<DGroups>

    // Обновить только флаг isSynced (и, возможно, id, если сервер вернул новый)
    @Query("UPDATE tgroups SET isSynced = 1, id = :newId WHERE id = :oldId")
    suspend fun markAsSynced(oldId: String, newId: String)

    @Query("DELETE FROM tgroups")
    suspend fun deleteAll()
}
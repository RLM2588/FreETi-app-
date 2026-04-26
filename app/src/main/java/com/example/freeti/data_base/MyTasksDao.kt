package com.example.freeti.data_base
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.freeti.data.local.entity.DTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface MyTasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DTasks>)

    @Query("SELECT MAX(updated_at) FROM tasks WHERE strftime('%Y-%m', start/1000, 'unixepoch') = :yearMonth")
    suspend fun getMaxUpdatedAtForMonth(yearMonth: String): Long?  // yearMonth = "2025-03"

    // Получить все задачи за месяц (для первоначальной загрузки)
    @Query("SELECT * FROM tasks WHERE strftime('%Y-%m', start/1000, 'unixepoch') = :yearMonth")
    suspend fun getTasksForMonth(yearMonth: String): List<DTasks>

    // Удалить все задачи за месяц (при очистке)
    @Query("DELETE FROM tasks WHERE strftime('%Y-%m', start/1000, 'unixepoch') = :yearMonth")
    suspend fun deleteTasksForMonth(yearMonth: String)

    // Удалить задачи на определенный месяц
    @Query("DELETE FROM tasks WHERE strftime('%Y-%m', start/1000, 'unixepoch') = :yearMonth")
    suspend fun deleteTasksOlderThan(yearMonth: String)

    // Получить все уникальные месяцы, для которых есть задачи (нужно для очистки)
    @Query("SELECT DISTINCT strftime('%Y-%m', start/1000, 'unixepoch') FROM tasks")
    suspend fun getExistingMonths(): List<String>

    // Flow всех задач (для UI)
    @Query("SELECT * FROM tasks WHERE is_delete = 0 ORDER BY start ASC")
    fun observeAllTasks(): Flow<List<DTasks>>

    @Query("SELECT * FROM tasks WHERE start >= :start AND start < :end AND privacy = :privacy AND is_delete = 0 AND time_end <> 0  ORDER BY start ASC, id ASC")
    fun getTasksForDateRange(start: Long, end: Long, privacy : String): Flow<List<DTasks>>


    @Query("SELECT * FROM tasks WHERE start >= :start AND start < :end AND is_delete = 0 AND time_end = 0 AND privacy = :privacy ORDER BY start ASC, id ASC")
    fun observeTasksWithoutTime(start: Long, end: Long, privacy: String): Flow<List<DTasks>>

    // Flow всех задач (для UI)
    @Query("SELECT * FROM tasks WHERE is_delete = 0 AND privacy = :privacy ORDER BY start ASC, id ASC")
    fun observeAllTasksPrivacy(privacy: String): Flow<List<DTasks>>

    // Все задачи с is_synced = false (неотправленные)
    @Query("SELECT * FROM tasks WHERE is_synced = 0")
    suspend fun getUnsyncedTasks(): List<DTasks>

    // Обновить флаг is_synced и updated_at для конкретной задачи
    @Query("UPDATE tasks SET is_synced = 1, updated_at = :newUpdatedAt WHERE id = :taskId")
    suspend fun markTaskSynced(taskId: String, newUpdatedAt: Long)

    // Получить список id задач с is_synced = false для защиты от перезаписи
    @Query("SELECT id FROM tasks WHERE is_synced = 0")
    suspend fun getUnsyncedTaskIds(): List<String>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): DTasks?
}
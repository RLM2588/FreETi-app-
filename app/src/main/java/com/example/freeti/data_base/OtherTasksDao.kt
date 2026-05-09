package com.example.freeti.data_base

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.freeti.data.local.entity.DOtherTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface OtherTasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DOtherTasks>)

    // Удалить все задачи за день (при очистке)
    @Query("DELETE FROM other_tasks WHERE strftime('%Y-%m-%d', start/1000, 'unixepoch') = :yearMonth AND user_id = :id")
    suspend fun deleteTasksForMonth(yearMonth: String, id: Int)

    // Flow всех задач (для UI)
    @Query("SELECT * FROM other_tasks ORDER BY start ASC")
    fun observeAllUsersTasks(): Flow<List<DOtherTasks>>

    @Query("SELECT * FROM other_tasks WHERE user_id = :id AND start >= :start AND start < :end AND privacy = :privacy AND time_end <> 0  ORDER BY start ASC, id ASC")
    fun getTasksForDateRange(start: Long, end: Long, privacy : String, id: Int): Flow<List<DOtherTasks>>

    @Query("SELECT * FROM other_tasks WHERE user_id = :id AND privacy = :privacy ORDER BY start ASC, id ASC")
    fun observeAllTasksPrivacy(privacy: String, id: Int): Flow<List<DOtherTasks>>

    @Query("SELECT * FROM other_tasks WHERE user_id = :id")
    suspend fun observeAllTasks(id: Int): List<DOtherTasks>
}
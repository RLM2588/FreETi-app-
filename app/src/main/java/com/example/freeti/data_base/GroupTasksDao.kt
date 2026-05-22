package com.example.freeti.data_base

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.freeti.data.local.entity.DGroupEvents
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupTasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DGroupEvents>)

    @Delete
    suspend fun delete(task: DGroupEvents)

    @Query("DELETE FROM group_events WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Query("SELECT COUNT(*) FROM group_events WHERE group_id = :id")
    suspend fun count(id: String): Int

    // Удалить все задачи за день (при очистке)
    @Query("DELETE FROM group_events WHERE strftime('%Y-%m-%d', start/1000, 'unixepoch') = :yearMonth AND group_id = :id")
    suspend fun deleteTasksForMonth(yearMonth: String, id: String)

    // Flow всех задач (для UI)
    @Query("SELECT * FROM group_events ORDER BY start ASC")
    fun observeAllUsersTasks(): Flow<List<DGroupEvents>>

    @Query("SELECT * FROM group_events WHERE group_id = :id AND start >= :start AND start < :end ORDER BY start ASC")
    fun getTasksForDateRange(start: Long, end: Long, id: String): Flow<List<DGroupEvents>>

    @Query("SELECT * FROM group_events WHERE group_id = :id ORDER BY start ASC, id ASC")
    fun observeAllTasksPrivacy(id: String): Flow<List<DGroupEvents>>

    @Query("SELECT * FROM group_events WHERE group_id = :id")
    suspend fun observeAllTasks(id: String): List<DGroupEvents>
}
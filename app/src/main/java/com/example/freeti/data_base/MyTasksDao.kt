package com.example.freeti.data_base
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.freeti.data.local.entity.DTasks

@Dao
interface MyTasksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<DTasks>)

    // Брать задачи на день, с фиксированным временем
    @Query("SELECT * FROM tasks WHERE privacy = :privacy AND start > :start AND start < :time_end AND time_end > 0 AND is_delete = True")
    suspend fun getTasksDay(privacy: String, start: Long, time_end: Long): List<DTasks> // Брать все или на один день??

    // Брать задачи на день без времени конца
    @Query("SELECT * FROM tasks WHERE privacy = :privacy AND start > :start AND start < :time_end AND time_end = 0 AND is_delete = True")
    suspend fun getTasksDayNoTime(privacy: String, start: Long, time_end: Long): List<DTasks> // Брать все или на один день?
}
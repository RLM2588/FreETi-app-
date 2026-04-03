package com.example.freeti.data_base
import androidx.room.Dao
import androidx.room.Query
import com.example.freeti.data.local.entity.DTasks

@Dao
interface TasksDao {
    // Брать задачи на день, с фиксированным временем
    @Query("SELECT * FROM tasks WHERE user_id = :user_id AND private = :private AND start > :start AND start < :start AND time_end > 0")
    suspend fun getTasksDay(user_id: Int, private: String, start: Long, end: Long): List<DTasks> // Брать все или на один день??

    // Брать задачи на день без времени конца
    @Query("SELECT * FROM tasks WHERE user_id = :user_id AND private = :private AND start > :start AND start < :start AND time_end = 0")
    suspend fun getTasksDayNoTime(user_id: Int, private: String, start: Long, end: Long): List<DTasks> // Брать все или на один день?
}
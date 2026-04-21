package com.example.freeti.data.local.entity
import androidx.room.Entity

@Entity(primaryKeys = ["task", "repeat_task"], tableName = "edited_tasks")
data class DEditedTasks (
    var task: String,
    var repeat_task: String
) // договориться об именах полей
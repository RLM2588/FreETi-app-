package com.example.freeti.data.local.entity
import androidx.room.Entity

@Entity(primaryKeys = ["task1", "repeattask"], tableName = "edited_tasks")
data class DEditedTasks (
    val task1: String,
    val repeattask: String
)
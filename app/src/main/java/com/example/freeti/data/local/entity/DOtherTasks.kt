package com.example.freeti.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "other_tasks")
data class DOtherTasks (
    @PrimaryKey
    var id: String,
    var title: String,
    var body: String,
    var user_id: Int,
    var start: Long, //timestamp
    var time_end : Long, //timestamp
    var status: String,
    var privacy: String,
    var importance: Int,
    var push_template_id: Int,
    @ColumnInfo(defaultValue = "FFFFFF")
    var colour: String,
    var updated_at: Long
)
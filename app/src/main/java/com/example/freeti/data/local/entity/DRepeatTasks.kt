package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeti.enum_classes.EPrivacy


@Entity(tableName = "repeat_tasks")
data class DRepeatTasks (
    @PrimaryKey
    var id: String,
    var title: String,
    var body: String,
    var user_id: Int,
    var start: Long, //timestamp
    var end : Long, //timestamp
    var global_end : Long, //timestamp
    var repeat_time: Int,
    var privacy: String,
    var push_template_id: Int,
    var created_at: Long //timestamp !!! надо ли возможность null? нужно ли вообще это поле локально?
)
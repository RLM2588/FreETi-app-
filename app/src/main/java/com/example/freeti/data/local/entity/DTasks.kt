package com.example.freeti.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeti.enum_classes.EPrivacy
import com.example.freeti.enum_classes.EStatus


@Entity(tableName = "tasks")
data class DTasks (
    @PrimaryKey
    var id: String,
    var title: String,
    var body: String,
    var user_id: Int,
    var start: Long, //timestamp
    var time_end : Long, //timestamp
    var status: String,// TODO сделать enum надо + остальные поля, остановился тут, узнать как сделать конвертер и тп
    var private: String,
    var importance: Int,
    var push_template_id: Int,
    @ColumnInfo(defaultValue = "FFFFFF")
    var colour: String,
    var created_at: Long //timestamp !!! надо ли возможность null? нужно ли вообще это поле локально?
)
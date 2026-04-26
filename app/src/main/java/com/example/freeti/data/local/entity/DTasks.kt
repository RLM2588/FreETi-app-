package com.example.freeti.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeti.enum_classes.EPrivacy
import com.example.freeti.enum_classes.EStatus
import com.example.freeti.network_entity.NTasks


@Entity(tableName = "tasks")
data class DTasks (
    @PrimaryKey
    var id: String,
    var title: String,
    var body: String,
    var start: Long, //timestamp
    var time_end : Long, //timestamp
    var status: String,
    var privacy: String,
    var importance: Int,
    var push_template_id: Int,
    @ColumnInfo(defaultValue = "FFFFFF")
    var colour: String,
    var updated_at: Long,
    var is_delete: Boolean,
    @ColumnInfo(defaultValue = "1")   // новое поле
    var is_synced: Boolean = true
) {
    fun toNetworkEntity(): NTasks {
        return NTasks(
            id = this.id,
            title = this.title,
            body = this.body,
            start = this.start,
            time_end = this.time_end,
            status = this.status,
            privacy = this.privacy,
            importance = this.importance,
            push_template_id = this.push_template_id,
            colour = this.colour,
            updated_at = this.updated_at,
            is_delete = this.is_delete
        )
    }
}
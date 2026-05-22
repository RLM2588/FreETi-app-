package com.example.freeti.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeti.network_entity.NGroupEvents


@Entity(tableName = "group_events")
data class DGroupEvents (
    @PrimaryKey
    var id: String,
    var title: String,
    var body: String,
    var group_id: String,
    var start: Long,
    var time_end: Long,
    var status: String,
    var importance: Int,
    @ColumnInfo(defaultValue = "FFFFFF")
    var colour: String,
    var vote_id: String,
    var creatby_user_id: Int
) {
    fun toNetworkEntity(): NGroupEvents {
        return NGroupEvents(
            id = this.id,
            title = this.title,
            body = this.body,
            group_id = this.group_id,
            start = this.start,
            time_end = this.time_end,
            status = this.status,
            importance = this.importance,
            colour = this.colour,
            vote_id = this.vote_id,
            creatby_user_id = this.creatby_user_id
        )
    }
}
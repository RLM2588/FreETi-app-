package com.example.freeti.network_entity

import com.example.freeti.data.local.entity.DOtherTasks
import com.example.freeti.data.local.entity.DTasks

data class NOtherTasks (
    val id: String,
    val title: String,
    val body: String,
    val user_id: Int,
    val start: Long, //timestamp
    val time_end : Long, //timestamp
    val status: String,
    val privacy: String,
    val importance: Int,
    val push_template_id: Int,
    val colour: String
) {
    fun toEntity() : DOtherTasks {
        return DOtherTasks(
            id = this.id,
            title = this.title,
            body = this.body,
            user_id = this.user_id,
            start = this.start,
            time_end = this.time_end,
            status = this.status, // преобразуем строку в enum
            privacy = this.privacy,
            importance = this.importance,
            push_template_id = this.push_template_id,
            colour = this.colour
        )
    }
}
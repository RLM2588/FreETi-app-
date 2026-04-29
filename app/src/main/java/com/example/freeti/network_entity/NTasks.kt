package com.example.freeti.network_entity

import com.example.freeti.data.local.entity.DTasks

data class NTasks (
    val id: String,
    val title: String,
    val body: String,
    val start: Long,
    val time_end : Long,
    val status: String,
    val privacy: String,
    val importance: Int,
    val push_template_id: Int,
    val colour: String,
    val updated_at: Long, //timestamp !!! надо ли возможность null? нужно ли вообще это поле локально?
    val is_delete: Boolean
) {
    fun toEntity() : DTasks {
        return DTasks(
            id = this.id,
            title = this.title,
            body = this.body,
            start = this.start,
            time_end = this.time_end,
            status = this.status, // преобразуем строку в enum
            privacy = this.privacy,
            importance = this.importance,
            push_template_id = this.push_template_id,
            colour = this.colour,
            updated_at = this.updated_at,
            is_delete = this.is_delete
        )
    }
}
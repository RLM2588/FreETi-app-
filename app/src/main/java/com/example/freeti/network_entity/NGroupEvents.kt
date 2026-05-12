package com.example.freeti.network_entity

import com.example.freeti.data.local.entity.DGroupEvents


data class NGroupEvents (
    var id: String,
    var title: String,
    var body: String,
    var group_id: String,
    var start: Long,
    var time_end: Long,
    var status: String, // TODO status enum, хз, то ли
    var importance: Int,
    var colour: String = "FFFFFF",
    var vote_id: String,
    var creatby_user_id: Int
) {
    fun toEntity() : DGroupEvents {
        return DGroupEvents(
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
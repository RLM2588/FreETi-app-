package com.example.freeti.network_entity

data class NOtherTasks (
    val id: String,
    val title: String?,
    val body: String?,
    val user_id: Int,
    val start: Long?, //timestamp
    val time_end : Long?, //timestamp
    val status: String,
    val privacy: String,
    val importance: Int,
    val push_template_id: Int?,
    val colour: String
)
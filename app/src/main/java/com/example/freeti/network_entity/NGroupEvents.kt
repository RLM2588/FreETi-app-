package com.example.freeti.network_entity

data class NGroupEvents (
    val id: String,
    val title: String,
    val body: String?,
    val group_id: String,
    val start: Long,
    val status: String, // TODO enum или string???
    val end: Long,
    val vote_id: String,
    val created_at: String,
    val creatby_user_id: Int
)
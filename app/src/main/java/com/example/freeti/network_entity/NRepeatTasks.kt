package com.example.freeti.network_entity

data class NRepeatTasks (
    val id: String,
    val title: String?,
    val body: String?,
    val user_id: Int,
    val start: Long?, //timestamp
    val end : Long?, //timestamp
    val global_end : Long?, //timestamp
    val repeat: Int,
    val private: String,
    val push_template_id: Int?,
    val created_at: Long //timestamp !!! надо ли возможность null? нужно ли вообще это поле локально?
)
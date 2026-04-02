package com.example.freeti.network_entity

data class NTasks (
    val id: String,
    val title: String?,
    val body: String?,
    val user_id: Int,
    val start: Long?, //timestamp
    val time_end : Long?, //timestamp
    val status: String,// TODO enum / string?
    val private: String,
    val importance: Int,
    val push_template_id: Int?,
    val colour: String,
    val created_at: Long //timestamp !!! надо ли возможность null? нужно ли вообще это поле локально?
)
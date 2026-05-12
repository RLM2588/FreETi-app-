package com.example.freeti.data_base

data class UserWithContactStatus(
    val id: Int,
    val login: String,
    val username: String,
    val avatar: String,
    val isFriend: Boolean
)
package com.example.freeti.network_entity

import com.example.freeti.data.local.entity.DUsers

data class NUsers (
    val id: Int,
    val username: String,
    val avatar: String
) {
    fun toEntity(): DUsers {
        return DUsers(id = this.id,
            username = this.username,
            avatar = this.avatar)
    }
}
package com.example.freeti.network_entity

import com.example.freeti.data.local.entity.DGroupsUsers

data class NGroupsUsers (
    val group: String,
    val user1: Int,
    val role: String //TODO String enum??
) {
    fun toEntity() : DGroupsUsers = DGroupsUsers(
        tgroup = this.group,
        user1 = this.user1,
        role = this.role
    )
}
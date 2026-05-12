package com.example.freeti.network_entity

import com.example.freeti.data.local.entity.DContacts

data class NContacts (
    val user1: Int,
    val user2: Int,
    val isFriend: Boolean = false
) {
    fun toEntity(): DContacts = DContacts(
        user1 = this.user1,
        user2 = this.user2,
        isFriend = this.isFriend
    )
}
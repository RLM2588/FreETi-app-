package com.example.freeti.network_entity

import com.example.freeti.data.local.entity.DGroups

data class NGroups (
    val id: String,
    val title: String,
    val body: String,
    val is_deleted: Boolean = false
) {
    fun toEntity(): DGroups {
        return DGroups(
            id = this.id,
            title = this.title,
            body = this.body,
            is_deleted = this.is_deleted
        )
    }
}
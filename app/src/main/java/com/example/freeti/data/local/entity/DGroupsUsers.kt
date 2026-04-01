package com.example.freeti.data.local.entity
import androidx.room.Entity
import com.example.freeti.enum.ERole

@Entity(primaryKeys = ["group", "user1"], tableName = "groups_users")
data class DGroupsUsers (
    val group: String,
    val user1: Int,
    val role: ERole
)
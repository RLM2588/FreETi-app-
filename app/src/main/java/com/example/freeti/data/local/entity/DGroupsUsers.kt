package com.example.freeti.data.local.entity
import androidx.room.Entity
import com.example.freeti.enum_classes.ERole

@Entity(primaryKeys = ["group", "user1"], tableName = "groups_users")
data class DGroupsUsers (
    var group: String,
    var user1: Int,
    var role: String
)
package com.example.freeti.data.local.entity
import androidx.room.Entity
import com.example.freeti.enum_classes.ERole

@Entity(primaryKeys = ["tgroup", "user1"], tableName = "groups_users")
data class DGroupsUsers (
    var tgroup: String,
    var user1: Int,
    var role: String = "MEMBER"
)
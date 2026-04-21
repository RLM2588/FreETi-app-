package com.example.freeti.data.local.entity
import androidx.room.Entity

@Entity(primaryKeys = ["user1", "user2"], tableName = "contacts")
data class DContacts (
    var user1: Int,
    var user2: Int
)
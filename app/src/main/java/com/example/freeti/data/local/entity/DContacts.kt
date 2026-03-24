package com.example.freeti.data.local.entity
import androidx.room.Entity

@Entity(primaryKeys = ["user1", "user2"], tableName = "contacts")
data class DContacts (
    val user1: Int,
    val user2: Int
)
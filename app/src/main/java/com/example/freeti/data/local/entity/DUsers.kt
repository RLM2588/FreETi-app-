package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class DUsers (
    @PrimaryKey
    var id: Int,
    var username: String,
    var avatar_id: Int
)
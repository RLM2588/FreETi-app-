package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class DUsers (
    @PrimaryKey
    val id: Int,
    val username: String,
    val avatar_id: Int
)
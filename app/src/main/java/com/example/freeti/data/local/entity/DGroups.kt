package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "groups")
data class DGroups (
    @PrimaryKey
    val id: String,
    val title: String,
    val body: String?
)
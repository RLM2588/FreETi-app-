package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "groups")
data class DGroups (
    @PrimaryKey
    var id: String,
    var title: String,
    var body: String
)
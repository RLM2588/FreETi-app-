package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "avatar")
data class DAvatar (
    @PrimaryKey
    var id: Int,
    var webadress: String
)
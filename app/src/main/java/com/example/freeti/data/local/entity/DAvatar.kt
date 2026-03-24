package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "avatar")
data class DAvatar (
    @PrimaryKey
    val id: Int,
    val webadress: String
)
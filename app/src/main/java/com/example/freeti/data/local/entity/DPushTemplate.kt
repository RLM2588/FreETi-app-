package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "push_template")
data class DPushTemplate (
    @PrimaryKey
    val id: Int,
    val before_how: Long
)

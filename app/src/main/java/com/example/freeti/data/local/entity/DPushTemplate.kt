package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "push_template")
data class DPushTemplate (
    @PrimaryKey
    var id: Int,
    var before_how: Long
)

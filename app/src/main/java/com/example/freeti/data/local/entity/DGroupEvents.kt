package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeti.enum.EStatus


@Entity(tableName = "group_events")
data class DGroupEvents (
    @PrimaryKey
    val id: String,
    val title: String,
    val body: String?,
    val group_id: String,
    val start: Long,
    val status: EStatus, // TODO status enum, хз, то ли
    val end: Long,
    val vote_id: String,
    val created_at: String,
    val creatby_user_id: Int
)
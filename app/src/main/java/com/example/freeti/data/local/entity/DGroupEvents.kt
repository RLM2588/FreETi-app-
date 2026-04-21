package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "group_events")
data class DGroupEvents (
    @PrimaryKey
    var id: String,
    var title: String,
    var body: String,
    var group_id: String,
    var start: Long,
    var status: String, // TODO status enum, хз, то ли
    var end: Long,
    var vote_id: String,
    var created_at: String,
    var creatby_user_id: Int
)
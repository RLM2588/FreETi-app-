package com.example.freeti.data.local.entity
import androidx.room.Entity

@Entity(primaryKeys = ["user_id", "voting_id"], tableName = "vote")
data class DVote (
    var user_id: Int,
    var choose: Int,
    var voting_id: String
)
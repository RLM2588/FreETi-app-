package com.example.freeti.data.local.entity
import androidx.room.Entity

@Entity(primaryKeys = ["user_id", "voting_id"], tableName = "vote")
data class DVote (
    val user_id: Int,
    val choose: Int,
    val voting_id: String
)
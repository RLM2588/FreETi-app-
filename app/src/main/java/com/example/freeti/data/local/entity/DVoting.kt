package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "voting")
data class DVoting (
    @PrimaryKey
    val id: String,
    val title: String,
    val var1: String,
    val var2: String,
    val var3: String?,
    val var4: String?,
    val var5: String?,
    val group_id: String,
    //TODO status???
)
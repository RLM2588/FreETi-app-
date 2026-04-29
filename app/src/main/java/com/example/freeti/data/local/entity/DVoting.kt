package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeti.enum_classes.EStatus


@Entity(tableName = "voting")
data class DVoting (
    @PrimaryKey
    var id: String,
    var title: String,
    var var1: String,
    var var2: String,
    var var3: String,
    var var4: String,
    var var5: String,
    var group_id: String,
    var status: String // точно ли такой?
)
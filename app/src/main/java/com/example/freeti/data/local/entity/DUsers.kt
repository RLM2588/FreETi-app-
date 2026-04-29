package com.example.freeti.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeti.network_entity.NUsers


@Entity(tableName = "users")
data class DUsers (
    @PrimaryKey
    var id: Int,
    var username: String,
    var avatar: String
) {
    fun toNetworkEntity(): NUsers {
        return NUsers(id = this.id,
            username = this.username,
            avatar = this.avatar)
    }

    fun defaultUser(id: Int) = DUsers(
        id = id,
        username = "User",
        avatar = ":)"
    )
}
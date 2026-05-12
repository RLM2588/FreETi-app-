package com.example.freeti.data.local.entity
import androidx.room.Entity
import com.example.freeti.network_entity.NContacts

@Entity(primaryKeys = ["user1", "user2"], tableName = "contacts")
data class DContacts (
    var user1: Int,
    var user2: Int,
    var isFriend: Boolean = false,
    var is_synced: Boolean = true
) {
    fun toNetworkEntity(): NContacts = NContacts(
        user1 = this.user1,
        user2 = this.user2,
        isFriend = this.isFriend
    )
}
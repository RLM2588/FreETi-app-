package com.example.freeti.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.freeti.R
import com.example.freeti.network_entity.NGroups


@Entity(tableName = "tgroups")
data class DGroups (
    @PrimaryKey
    var id: String,
    var title: String,
    var body: String,
    var is_deleted: Boolean = false,
    var isSynced: Boolean = true,
    var imageResId: Int = R.drawable.groups_logo1_pn
) {
    fun toNetworkEntity(): NGroups {
        return NGroups(
            id = this.id,
            title = this.title,
            body = this.body,
            is_deleted = this.is_deleted
        )
    }
}
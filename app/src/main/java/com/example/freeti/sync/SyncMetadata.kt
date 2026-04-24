package com.example.freeti.sync

import androidx.room.Entity
import androidx.room.PrimaryKey

// https://chat.deepseek.com/a/chat/s/a85d5dcd-21a0-497e-a6df-b29ee1f86df5
@Entity(tableName = "sync_metadata")
data class SyncMetadata(
    @PrimaryKey
    var yearMonth: String,          // "2025-03"
    var lastSyncAt: Long,           // timestamp последней синхронизации (мс)
    var lastAccessAt: Long,          // timestamp последнего открытия (мс)
    var last_updated_at: Long
)
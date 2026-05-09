package com.example.freeti.data_base
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.freeti.sync.SyncMetadata

@Dao
interface SyncMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(metadata: SyncMetadata)

    @Query("SELECT * FROM sync_metadata WHERE yearMonth = :yearMonth")
    suspend fun getMetadata(yearMonth: String): SyncMetadata?

    @Query("DELETE FROM sync_metadata WHERE yearMonth = :yearMonth")
    suspend fun deleteMetadata(yearMonth: String)

    @Query("SELECT yearMonth FROM sync_metadata WHERE lastAccessAt < :threshold")
    suspend fun getMonthsOlderThan(threshold: Long): List<String>
}
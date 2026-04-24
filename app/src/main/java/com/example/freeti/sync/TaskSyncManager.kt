package com.example.freeti.sync

import com.example.freeti.data_base.MyTasksDao
import com.example.freeti.data_base.SyncMetadataDao
import com.example.freeti.network_api.ApiService

class TaskSyncManager(
    private val myTaskDao: MyTasksDao,
    private val metadataDao: SyncMetadataDao,
    private val apiService: ApiService,
    private val syncConfig: SyncConfig = SyncConfig() // настройки: minSyncIntervalMs = 5*60*1000, staleThresholdMs = 7*24*60*60*1000
) {
    // Главный метод: синхронизировать месяц (вызывается из ViewModel, когда пользователь открывает экран)
    suspend fun syncMonth(yearMonth: String, force: Boolean = false) {
        try {
            val metadata = metadataDao.getMetadata(yearMonth)
            val now = System.currentTimeMillis()
            if (!force && metadata != null && now - metadata.lastSyncAt < syncConfig.minSyncIntervalMs) {
                return
            }

            val tasksFromNetwork = if (metadata == null) {
                apiService.getTasksForMonth(yearMonth)
            } else {
                apiService.getTasksForMonthSince(yearMonth, metadata.last_updated_at)
            }

            val entities = tasksFromNetwork.map { it.toEntity() }
            myTaskDao.insertAll(entities)
            val time_updated = myTaskDao.getMaxUpdatedAtForMonth(yearMonth)
            if (time_updated != null) {
                metadataDao.upsert(
                    SyncMetadata(
                        yearMonth = yearMonth,
                        lastSyncAt = now,
                        lastAccessAt = now,
                        last_updated_at = time_updated
                    )
                )
            }
        } catch (e: Exception) {
            // Просто логируем, не мешаем оффлайн-работе
            e.printStackTrace()
            android.util.Log.w("TaskSyncManager", "Sync failed for $yearMonth: ${e.message}")
        }
    }

    // Метод для отметки, что пользователь просматривал месяц (без синхронизации)
    suspend fun markMonthAccessed(yearMonth: String) {
        val metadata = metadataDao.getMetadata(yearMonth)
        if (metadata != null) {
            metadataDao.upsert(metadata.copy(lastAccessAt = System.currentTimeMillis()))
        } else {
            // Если месяц ещё не синхронизировался, но мы его просто открыли, можно создать запись с нулевой синхронизацией
            val time_updated = myTaskDao.getMaxUpdatedAtForMonth(yearMonth)
            if (time_updated != null) {
                metadataDao.upsert(
                    SyncMetadata(
                        yearMonth = yearMonth,
                        lastSyncAt = 0,
                        lastAccessAt = System.currentTimeMillis(),
                        last_updated_at = time_updated
                    )
                )
            }
        }
    }

    // Очистка старых чанков (запускать по расписанию через WorkManager)
    suspend fun cleanOldChunks() {
        val now = System.currentTimeMillis()
        val threshold = now - syncConfig.staleThresholdMs  // например, 7 дней
        val oldMonths = metadataDao.getMonthsOlderThan(threshold)
        for (month in oldMonths) {
            myTaskDao.deleteTasksForMonth(month)
            metadataDao.deleteMetadata(month)
        }
    }
}

data class SyncConfig(
    val minSyncIntervalMs: Long = 5 * 60 * 1000,      // 5 минут
    val staleThresholdMs: Long = 7 * 24 * 60 * 60 * 1000  // 7 дней
)
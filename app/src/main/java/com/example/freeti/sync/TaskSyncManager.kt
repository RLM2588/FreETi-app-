package com.example.freeti.sync

import com.example.freeti.data_base.MyTasksDao
import com.example.freeti.data_base.SyncMetadataDao
import com.example.freeti.network_api.ApiService
import com.example.freeti.network_entity.NTasks

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

            // Получаем список id локальных неотправленных задач
            val unsyncedIds = myTaskDao.getUnsyncedTaskIds().toSet()
            // Фильтруем сетевые задачи, оставляя только те, которые не конфликтуют
            if (tasksFromNetwork.body() != null) {

                val safeTasks = tasksFromNetwork.body()!!.filter { it.id !in unsyncedIds }
                //val safeTasks = tasksFromNetwork.body();

                val entities = safeTasks.map { it.toEntity() }
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
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    suspend fun syncPendingTasks() {
        try {
            val pendingTasks = myTaskDao.getUnsyncedTasks()
            for (task in pendingTasks) {
                // Преобразуем в NTasks (нужен метод toNetworkEntity или аналогичный)
                val networkTask = task.toNetworkEntity()  // нужно реализовать
                val response = apiService.updateTask(networkTask)
                if (response.isSuccessful) {
                    val updatedTask = response.body()
                    if (updatedTask != null) {
                        myTaskDao.markTaskSynced(task.id, updatedTask.updated_at)
                    }
                } else {
                    // Ошибка – оставляем задачу в очереди
                    continue
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

data class SyncConfig(
    val minSyncIntervalMs: Long = 5 * 60 * 1000,      // 5 минут
    val staleThresholdMs: Long = 7 * 24 * 60 * 60 * 1000  // 7 дней
)
package com.example.freeti.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.freeti.sync.TaskSyncManager

class CleanupWorker(
    context: Context,
    params: WorkerParameters,
    private val syncManager: TaskSyncManager
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            syncManager.cleanOldChunks()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
package com.example.freeti

import android.app.Application
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.freeti.data_base.AppDataBase
import com.example.freeti.network_api.NetworkClient
import com.example.freeti.repository.AuthRepository
import com.example.freeti.repository.TestRepository
import com.example.freeti.sync.SyncConfig
import com.example.freeti.sync.TaskSyncManager
import com.example.freeti.tokens.TokenManager
import com.example.freeti.worker.CleanupWorker
import java.util.concurrent.TimeUnit

class AppContainer(private val context: Context) {
    private val database = AppDataBase.getInstance(context)

    private val tasksDao = database.tasksDao()
    val myTasksDao = database.myTasksDao()
    private val syncMetaDao = database.syncMetadataDao()

    private val tokenManager = TokenManager(context)

    private val apiService = NetworkClient.provideApiService()

    val authRepository = AuthRepository(apiService, tokenManager)
    val testRepository = TestRepository(apiService)


    val taskSyncManager = TaskSyncManager(myTasksDao, syncMetaDao, apiService,
        syncConfig = SyncConfig()
    )
}

class MyApp: Application() {
    companion object {
        lateinit var instance: MyApp
            private set

        val container: AppContainer
            get() = instance.appContainer

        fun getAppContext(): Context = instance.applicationContext
    }
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        appContainer = AppContainer(this)
    }

    private fun scheduleDailyCleanup() {
        val cleanupRequest = PeriodicWorkRequestBuilder<CleanupWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "task_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }
}
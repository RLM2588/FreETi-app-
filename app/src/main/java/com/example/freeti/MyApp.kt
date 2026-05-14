package com.example.freeti

import android.app.Application
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.freeti.data_base.AppDataBase
import com.example.freeti.network_api.ApiService
import com.example.freeti.network_api.NetworkClient
import com.example.freeti.repository.AuthRepository
import com.example.freeti.repository.ContactsRepository
import com.example.freeti.repository.GroupRepository
import com.example.freeti.repository.GroupTaskRepository
import com.example.freeti.repository.MembersRepository
import com.example.freeti.repository.OtherTaskRepository
import com.example.freeti.repository.SearchRepository
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
    val otherTaskDao = database.otherTaskDao()
    val groupsDao = database.groupsDao()
    val groupTasksDao = database.groupTasksDao()
    val groupMemberDao = database.groupMembersDao()
    private val syncMetaDao = database.syncMetadataDao()

    val userDao = database.usersDao()

    val contactsDao = database.contactsDao()

    val tokenManager = TokenManager(context)

    val apiService: ApiService = NetworkClient.provideApiService(tokenManager) {
        authRepository
    }

    val authRepository = AuthRepository(apiService, tokenManager)

    val testRepository = TestRepository(apiService)
    val otherRepository = OtherTaskRepository(apiService, otherTaskDao, userDao)

    val groupRepository = GroupRepository(groupsDao, apiService)
    val groupTaskRepository = GroupTaskRepository(apiService, groupTasksDao)
    val membersRepository = MembersRepository(apiService, groupMemberDao, userDao)
    val contactsRepository = ContactsRepository(contactsDao, userDao, apiService, tokenManager)

    val searchRepository = SearchRepository(
        userDao,
        apiService,
        tokenManager
    )

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
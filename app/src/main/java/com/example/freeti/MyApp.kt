package com.example.freeti

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.freeti.data_base.AppDataBase
import com.example.freeti.events.AuthEvent
import com.example.freeti.events.AuthEventBus
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AppContainer(private val context: Context) {
    val database = AppDataBase.getInstance(context)

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

    var currentActivity: Activity? = null
        private set

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        appContainer = AppContainer(this)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                if (currentActivity == activity) currentActivity = null
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })

        CoroutineScope(Dispatchers.Main).launch {
            AuthEventBus.events.collect { event ->
                when (event) {
                    is AuthEvent.TokenRefreshFailed -> showTokenErrorDialog()
                }
            }
        }
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

    private fun showTokenErrorDialog() {
        val activity = currentActivity ?: return
        if (activity.isFinishing || activity.isDestroyed) return

        AlertDialog.Builder(activity)
            .setTitle("Ошибка авторизации")
            .setMessage("Не удалось обновить токены. Выберите действие?")
            .setPositiveButton("Выйти и очистить кэш") { _, _ ->
                logoutAndClearCache()
            }
            .setNegativeButton("Выйти без очистки") { _, _ ->
                logoutWithoutClear()
            }
            .setNeutralButton("Продолжить офлайн") { _, _ ->
            }
            .setCancelable(false)
            .show()
    }

    private fun logoutAndClearCache() {
        val activity = currentActivity ?: return

        appContainer.tokenManager.clearTokens()

        CoroutineScope(Dispatchers.IO).launch {
            appContainer.database.clearAll_Tables()
        }

        val intent = android.content.Intent(activity, MainActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finishAffinity()
    }

    private fun logoutWithoutClear() {
        val activity = currentActivity ?: return
        appContainer.tokenManager.clearTokens()

        val intent = android.content.Intent(activity, MainActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finishAffinity()
    }
}
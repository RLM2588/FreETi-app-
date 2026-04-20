package com.example.freeti

import android.app.Application
import android.content.Context
import com.example.freeti.data_base.AppDataBase
import com.example.freeti.network_api.NetworkClient
import com.example.freeti.repository.AuthRepository
import com.example.freeti.repository.TestRepository
import com.example.freeti.tokens.TokenManager

class AppContainer(private val context: Context) {
    private val database = AppDataBase.getInstance(context)

    private val tasksDao = database.tasksDao()

    private val tokenManager = TokenManager(context)

    private val apiService = NetworkClient.provideApiService()

    val authRepository = AuthRepository(apiService, tokenManager)
    val testRepository = TestRepository(apiService)
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
}
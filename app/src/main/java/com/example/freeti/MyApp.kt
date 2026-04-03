package com.example.freeti

import android.app.Application
import com.example.freeti.tokens.TokenManager

class MyApp: Application() {
    lateinit var tokenManager: TokenManager
        private set

    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(this)
    }
}
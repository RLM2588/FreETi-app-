package com.example.freeti.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class AuthEvent {
    object TokenRefreshFailed : AuthEvent()
}

object AuthEventBus {
    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: AuthEvent) {
        _events.emit(event)
    }
}
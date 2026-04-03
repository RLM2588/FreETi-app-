package com.example.freeti.network_entity

data class AuthResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val expires_in: Long? = null,   // опционально: через сколько секунд истекает access токен
    val user_id: Int?   // опционально: id пользователя
)
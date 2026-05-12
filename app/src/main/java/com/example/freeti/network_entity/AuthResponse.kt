package com.example.freeti.network_entity

data class AuthResponse(
    val accessToken: String?,
    val refreshToken: String,
    val userId: Int,
    val login: String
)
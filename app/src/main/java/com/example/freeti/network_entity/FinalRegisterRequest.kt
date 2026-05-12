package com.example.freeti.network_entity

data class FinalRegisterRequest (
    val login: String,
    val email: String,
    val code: String,
    val password: String
)
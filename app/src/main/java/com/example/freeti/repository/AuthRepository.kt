package com.example.freeti.repository

import android.util.Log
import com.example.freeti.network_api.ApiService
import com.example.freeti.tokens.TokenManager
import com.example.freeti.network_entity.*

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(username: String, password: String): Result<Unit> {
        return try {
            val response = api.login(LoginRequest(username, password))

            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null) {
                    tokenManager.saveTokens(authResponse)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error: empty answer"))
                }
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Wrong login or password"
                    else -> "Server error: ${response.code()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Connection error"))
        }
    }

    suspend fun testRegister(login: String, email: String): String {
        var answ: String
        try {
            val response = api.register_resp(RegisterRequest(login, email))
            if (!response.isSuccessful)
                answ = "Not Success"
            else {
                val body = response.body()
                if (body != null)
                    answ = "OK " + body.message
                else answ = "Not Success"
            }
        } catch (e: Exception) {
            Log.d("error", e.message + " ")
            answ = "Can not connect"
        }
        return answ
    } //FinalRegisterRequest

    suspend fun finalRegister(
        login: String,
        email: String,
        code: String,
        password: String
    ): String {
        var answ: String
        try {
            val response = api.finalRegister(FinalRegisterRequest(login, email, code, password))
            if (response.isSuccessful) {
                if (response.body() != null) {
                    val authres = response.body() ?: AuthResponse("null", "null", -1, "null")
                    tokenManager.saveTokens(authres)
                    answ = "Success"
                } else {
                    answ = "Resp is null"
                }
            } else {
                answ = "Not Success"
            }
        } catch (e: Exception) {
            answ = "Can not connect"
        }
        return answ
    }

    suspend fun register(username: String, email: String): Result<Unit> {
        return try {
            val response = api.register(RegisterRequest(username, email))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Registration failed: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshToken(): Result<Unit> {
        val refreshToken = tokenManager.getRefreshToken()
            ?: return Result.failure(Exception("No refresh token"))
        return try {
            val response = api.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful) {
                Log.w("ok", "ok")
                response.body()?.let { tokenManager.saveTokens(it) }
                    ?: return Result.failure(Exception("Empty response"))
                Result.success(Unit)
            } else {
                if (response.code() in 400..403) {
                    // Неудачное обновление — разлогиниваем
                    tokenManager.clearTokens()
                    logout()
                }
                Result.failure(Exception("Refresh failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            //tokenManager.clearTokens()
            Result.failure(e)
        }
    }

    suspend fun logout() {
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken != null) {
            try {
                api.logout(RefreshTokenRequest(refreshToken))
            } catch (_: Exception) {
            }
        }
        tokenManager.clearTokens()
    }

    fun isLoggedIn(): Boolean = tokenManager.hasSession()
}
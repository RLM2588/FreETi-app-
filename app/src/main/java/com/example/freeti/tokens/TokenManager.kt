package com.example.freeti.tokens

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.freeti.network_entity.AuthResponse


class TokenManager(context: Context) {

    // 1. Создаем современный MasterKey
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // 2. Создаем EncryptedSharedPreferences с помощью нового ключа
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "freeti_tokens_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EXPIRES_IN = "expires_in"      // в секундах
        private const val KEY_TOKEN_ISSUED_AT = "issued_at"  // момент получения токена (мс)
    }

    fun saveTokens(authResponse: AuthResponse) {
        val now = System.currentTimeMillis()
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, authResponse.accessToken)
            .putString(KEY_REFRESH_TOKEN, authResponse.refreshToken)
            .putInt(KEY_USER_ID, authResponse.user_id?: 0)
            .putLong(KEY_EXPIRES_IN, authResponse.expires_in ?: 0L)
            .putLong(KEY_TOKEN_ISSUED_AT, now)
            .apply()
    }

    fun saveAccessToken(accessToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 0)
    fun getExpiresIn(): Long = prefs.getLong(KEY_EXPIRES_IN, 0L)
    fun getTokenIssuedAt(): Long = prefs.getLong(KEY_TOKEN_ISSUED_AT, 0L)
    fun getAuthResponse() : AuthResponse {
        return AuthResponse(getAccessToken(),
            getRefreshToken(), getExpiresIn(),getUserId())
    }

    fun isAccessTokenExpired(): Boolean {
        val issuedAt = getTokenIssuedAt()
        val expiresIn = getExpiresIn()
        if (issuedAt == 0L || expiresIn == 0L) return true // нет данных – считаем истёкшим
        val now = System.currentTimeMillis()
        val elapsedSeconds = (now - issuedAt) / 1000
        return elapsedSeconds >= expiresIn
    }

    fun clearTokens() {
        prefs.edit().clear().apply()
    }

    fun hasSession(): Boolean = getAccessToken() != null && getRefreshToken() != null
}
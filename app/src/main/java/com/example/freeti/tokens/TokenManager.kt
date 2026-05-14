package com.example.freeti.tokens

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.freeti.network_entity.AuthResponse
import java.io.File
import javax.crypto.AEADBadTagException
import androidx.core.content.edit

class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = createSafeEncryptedPrefs(context)
//    EncryptedSharedPreferences.create(
//        context,
//        "freeti_tokens_prefs",
//        masterKey,
//        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//    )

    private fun createSafeEncryptedPrefs(context: Context): SharedPreferences {
        val prefsFileName = "freeti_tokens_prefs"
        return try {
            EncryptedSharedPreferences.create(
                context,
                prefsFileName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: AEADBadTagException) {
            Log.w(
                "TokenManager",
                "EncryptedSharedPreferences corrupted, deleting and recreating",
                e
            )
            val prefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
            val prefsFile = File(prefsDir, "$prefsFileName.xml")
            val prefsBakFile = File(prefsDir, "$prefsFileName.xml.bak")
            prefsFile.delete()
            prefsBakFile.delete()

            EncryptedSharedPreferences.create(
                context,
                prefsFileName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_LOGIN = "login"

    }

    fun saveTokens(authResponse: AuthResponse) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, authResponse.accessToken)
                .putString(KEY_REFRESH_TOKEN, authResponse.refreshToken)
                .putInt(KEY_USER_ID, authResponse.userId)
                .putString(KEY_LOGIN, authResponse.login)
        }
    }

    fun saveUser(userId: Int?, login: String?) {
        Log.w("tokenManager updated", "userId: $userId, login: $login")
        if (userId != null && login != null) {
            prefs.edit {
                putInt(KEY_USER_ID, userId)
                    .putString(KEY_LOGIN, login)
            }
        }
    }

    fun saveAccessToken(accessToken: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getLogin(): String = prefs.getString(KEY_LOGIN, "null") ?: "null"

    fun clearTokens() {
        prefs.edit().clear().apply()
    }

    fun hasSession(): Boolean = getAccessToken() != null && getRefreshToken() != null
}
package com.example.freeti.repository

import android.util.Log
import com.example.freeti.network_api.ApiService
import com.example.freeti.network_entity.UpdateResponse

class UpdateRepository(
    private val api: ApiService) {

    suspend fun checkUpdate(currentVersion: String): UpdateResponse {
        return try {
            val response = api.getUpdate(currentVersion)
            if (response.isSuccessful) {
                response.body() ?: defaultUpdateResponse().also {
                    Log.w("UpdateRepository", "Update response body is null, using default")
                }
            } else {
                Log.e("UpdateRepository", "Update check failed with code ${response.code()}, using default")
                defaultUpdateResponse()
            }
        } catch (e: Exception) {
            Log.e("UpdateRepository", "Error checking update", e)
            defaultUpdateResponse()
        }
    }

    private fun defaultUpdateResponse() = UpdateResponse(
        status = "ok",
        latestVersion = "1",
        downloadUrl = "https://freeti.ru/"
    )
}
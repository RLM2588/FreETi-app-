package com.example.freeti.repository

import com.example.freeti.network_api.ApiService
import com.example.freeti.network_entity.TestRequest

class TestRepository(
    private val api: ApiService
) {
    suspend fun send(out_text: String): Result<String> {
        return try {
            val response = api.send(TestRequest(out_text))
            if (response.isSuccessful) {
                response.body()?.let { testResponse ->
                    Result.success(testResponse.input_text)
                } ?: Result.failure(Exception("Empty response"))
            } else {

                Result.failure(Exception("test failed" + response.errorBody().toString()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
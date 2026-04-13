package com.example.freeti.network

import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import android.util.Log

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d("OkHttp", "Request: ${request.method} ${request.url}")

        val response = chain.proceed(request)
        Log.d("OkHttp", "Response: ${response.code}")

        return response
    }
}
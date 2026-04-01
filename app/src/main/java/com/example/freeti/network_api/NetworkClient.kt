package com.example.freeti.network_api
// Файл: NetworkModule.kt?или так оставить

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// TODO доделать, доразобраться, не хватает настройки https и обработки токенов

object NetworkClient {
    // Базовый URL сервера
    private const val BASE_URL = "https://сервер.ru/api/"

    // Настройка OkHttpClient
    private fun provideOkHttpClient(): OkHttpClient {
        // Логгер: будет печатать в Logcat все детали запросов/ответов
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY // Логируем всё

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // Добавляем логгер

            // Настройка таймаутов (важно для плохого интернета)
            .connectTimeout(30, TimeUnit.SECONDS) // Таймаут на подключение
            .readTimeout(30, TimeUnit.SECONDS)    // Таймаут на чтение ответа
            .writeTimeout(30, TimeUnit.SECONDS)   // Таймаут на запись запроса

            // НАСТРОЙКА HTTPS

            .build()
    }

    // Создание Retrofit
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())          // Настроенный клиент
            .addConverterFactory(GsonConverterFactory.create()) // Конвертер JSON
            .build()
    }

    // Удобная функция для получения экземпляра нашего API-интерфейса
    fun provideApiService(): ApiService {
        return provideRetrofit().create(ApiService::class.java)
    }
}
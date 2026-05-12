package com.example.freeti.network_api
// Файл: NetworkModule.kt?или так оставить

import android.util.Base64
import com.example.freeti.MyApp
import com.example.freeti.repository.AuthRepository
import com.example.freeti.tokens.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.*


// TODO доделать, доразобраться, не хватает настройки https и обработки токенов

object NetworkClient {
    // Базовый URL сервера
    private const val BASE_URL = "http://192.168.1.36:8091/api/" // TODO вставить в будщем свой сервер


    fun provideApiService(tokenManager: TokenManager, authProvider: () -> AuthRepository): ApiService {
        val okHttpClient = provideOkHttpClient(tokenManager, authProvider)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun provideOkHttpClient(
        tokenManager: TokenManager,
        authProvider: () -> AuthRepository // Используем лямбду, чтобы избежать цикла
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()

            val token = tokenManager.getAccessToken()
            val request = if (token != null) {
                original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                original
            }

            val credentials = "user:very-strong-password"
            val basicAuth = "Basic " + Base64.encodeToString(
                credentials.toByteArray(), Base64.NO_WRAP
            )
            val requestWithAuth = original.newBuilder()
                .header("Authorization", basicAuth)
                .build()
            chain.proceed(request/*WithAuth*/)
        }

        val tokenAuthenticator = object : Authenticator {
            override fun authenticate(route: Route?, response: Response): Request? {
                // Если это уже вторая попытка с тем же токеном - выходим
                val currentToken = tokenManager.getAccessToken()
                if (response.request.header("Authorization") == "Bearer $currentToken") {
                    // Пытаемся обновить
                    val refreshResult = runBlocking {
                        authProvider().refreshToken() // Вызываем через лямбду
                    }

                    if (refreshResult.isSuccess) {
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer ${tokenManager.getAccessToken()}")
                            .build()
                    }
                }
                return null
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }


   /*
    //private lateinit var tokenManager: TokenManager
    private lateinit var authRepository: AuthRepository

    val tokenManager: TokenManager by lazy {
        (application as MyApp).appContainer.tokenManager
    }


    fun init(tokenManager: TokenManager, authRepository: AuthRepository) {
        this.tokenManager = tokenManager
        this.authRepository = authRepository
    }*/

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
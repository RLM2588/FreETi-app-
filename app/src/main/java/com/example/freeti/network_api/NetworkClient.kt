package com.example.freeti.network_api
// Файл: NetworkModule.kt?или так оставить

import android.util.Base64
import android.util.Log
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
        authProvider: () -> AuthRepository
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 1. ИНТЕРЦЕПТОР: Добавляет токен только там, где нужно
        val authInterceptor = Interceptor { chain ->
            val request = chain.request()
            if (request.header("Authorization") != null) {
                // Уже есть заголовок – не дублируем
                return@Interceptor chain.proceed(request)
            }
            val path = request.url.encodedPath
            if (path.contains("/auth/")) {
                return@Interceptor chain.proceed(request) // без токена
            }
            val token = tokenManager.getAccessToken()
            val newRequest = if (token != null) {
                request.newBuilder().header("Authorization", "Bearer $token").build()
            } else request
            chain.proceed(newRequest)
        }

        // 2. АУТЕНТИФИКАТОР: Умное обновление
        val tokenAuthenticator = Authenticator { _, response ->
            // Если мы уже 2 раза получили 401 для этого запроса - всё, стоп.
            if (response.responseCount >= 2) {
                return@Authenticator null
            }

            synchronized(this) {
                val currentToken = tokenManager.getAccessToken()
                val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

                // Если токен в памяти уже другой (кто-то обновил его, пока мы ждали synchronized)
                if (currentToken != requestToken) {
                    Log.d("auth", "new token is already taken")
                    return@Authenticator response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                // Пытаемся обновить токен реально
                val refreshResult = runBlocking {
                    try {
                        authProvider().refreshToken()
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }

                if (refreshResult.isSuccess) {
                    val newToken = tokenManager.getAccessToken()
                    Log.d("AUTH", "Refreshed successfully. New token: $newToken")
                    //Log.d("resp2", " ${tokenManager.getAccessToken()} ${tokenManager.getRefreshToken()}")

                    // Используем .header() — он заменяет существующий заголовок, а не добавляет второй
                    return@Authenticator response.request.newBuilder()
                        .removeHeader("Authorization") // На всякий случай удаляем старый
                        .addHeader("Authorization", "Bearer $newToken")
                        .build()
                }
                else {
                    null // Репозиторий сам вызовет logout при неудаче рефреша
                }
            }
        }
        Log.d("resp", " ${tokenManager.getAccessToken()} ${tokenManager.getRefreshToken()}")
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    // Хелпер для подсчета попыток
    private val Response.responseCount: Int
        get() {
            var result = 1
            var r = priorResponse
            while (r != null) {
                result++
                r = r.priorResponse
            }
            return result
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


    /*
    // Настройка OkHttpClient
    private fun provideOkHttpClient(): OkHttpClient {
        // Логгер: будет печатать в Logcat все детали запросов/ответов
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY // Логируем всё

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
                synchronized(this) {
                    val currentToken = tokenManager.getAccessToken()
                    // Если токен изменился другим потоком — используем его
                    if (currentToken != null &&
                        response.request.header("Authorization")?.removePrefix("Bearer ") != currentToken) {
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer $currentToken")
                            .build()
                    }

                    // Пытаемся обновить токен
                    val refreshResult = runBlocking {
                        authRepository.refreshToken()
                    }

                    if (refreshResult.isSuccess) {
                        val newToken = tokenManager.getAccessToken()!!
                        return response.request.newBuilder()
                            .header("Authorization", "Bearer $newToken")
                            .build()
                    } else {
                        // Не удалось обновить — разлогиниваем и не повторяем запрос
                        runBlocking { authRepository.logout() }
                        return null
                    }
                }
            }
        }


        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)     // обрабатывает 401
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
    }*/
}
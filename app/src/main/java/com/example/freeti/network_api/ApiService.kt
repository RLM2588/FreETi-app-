package com.example.freeti.network_api

import com.example.freeti.network_entity.AuthResponse
import com.example.freeti.network_entity.LoginRequest
import com.example.freeti.network_entity.NTasks
import com.example.freeti.network_entity.NUsers
import com.example.freeti.network_entity.TestRequest
import com.example.freeti.network_entity.TestResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("test/out_inp")
    suspend fun test(@Body request: TestRequest): Response<TestResponse>

    @GET("tasks")
    suspend fun getTasksForMonth(
        @Query("yearMonth") yearMonth: String
    ): List<NTasks>

    // Получить задачи за месяц, изменённые после указанного времени
    @GET("tasks")
    suspend fun getTasksForMonthSince(
        @Query("yearMonth") yearMonth: String,
        @Query("since") since: Long   // updated_at > since
    ): List<NTasks>

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: String,
        @Body task: NTasks
    ): Response<NTasks>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: NUsers
    ): Response<NUsers>

    @GET("users")
    suspend fun getUser(
        @Query("id") id: Int
    ): Response<NUsers>

    @GET("users")
    suspend fun getUsersSearch(
        @Query("username") username: String
    ): List<NUsers>
}
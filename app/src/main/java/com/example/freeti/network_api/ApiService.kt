package com.example.freeti.network_api

import com.example.freeti.data.local.entity.DTasks
import com.example.freeti.network_entity.AuthResponse
import com.example.freeti.network_entity.FinalRegisterRequest
import com.example.freeti.network_entity.LoginRequest
import com.example.freeti.network_entity.NRepeatTasks
import com.example.freeti.network_entity.NTasks
import com.example.freeti.network_entity.RefreshTokenRequest
import com.example.freeti.network_entity.RegisterRequest
import com.example.freeti.network_entity.TestRequest
import com.example.freeti.network_entity.TestResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<String>

    @POST("auth/final_register")
    suspend fun finalRegister(@Body request: FinalRegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Body request: RefreshTokenRequest): Response<ResponseBody>

    @GET("tasks/{id}")
    suspend fun getTasks(@Path("id") user_id: Int): List<NTasks>

    @GET("tasks/{id}/repeat") //TODO как лучше?
    suspend fun getRepeatTasks(@Path("id") user_id: Int): List<NRepeatTasks>

    @POST("tasks")
    suspend fun postTasks(@Body tasks: List<DTasks>) // TODO :List<DTasks>??

    @POST("tasks")
    suspend fun postTask(@Body tasks: DTasks) // TODO : DTasks??

    @POST("test")
    suspend fun send(@Body message : TestRequest): Response<TestResponse>

    @GET("hello")
    suspend fun test2(): Response<TestResponse>


//    @POST("hello")
//    suspend fun test2(@Body message : TestRequest): Response<TestResponse>


    @POST("test/out_inp")
    suspend fun test(@Body request: TestRequest): Response<TestResponse>
}
package com.example.freeti.network_api

import com.example.freeti.data.local.entity.DTasks
import com.example.freeti.network_entity.NRepeatTasks
import com.example.freeti.network_entity.NTasks
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("tasks/{id}")
    suspend fun getTasks(@Path("id") user_id: Int): List<NTasks>

    @GET("tasks/{id}/repeat") //TODO как лучше?
    suspend fun getRepeatTasks(@Path("id") user_id: Int): List<NRepeatTasks>

    @POST("tasks")
    suspend fun postTasks(@Body tasks: List<DTasks>) // TODO :List<DTasks>??

    @POST("tasks")
    suspend fun postTask(@Body tasks: DTasks) // TODO : DTasks??
}
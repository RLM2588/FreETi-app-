package com.example.freeti.network_api

import com.example.freeti.data.local.entity.DTasks
import com.example.freeti.network_entity.AuthResponse
import com.example.freeti.network_entity.FinalRegisterRequest
import com.example.freeti.network_entity.LoginRequest
import com.example.freeti.network_entity.NGroups
import com.example.freeti.network_entity.NOtherTasks
import com.example.freeti.network_entity.NRepeatTasks
import com.example.freeti.network_entity.NTasks
import com.example.freeti.network_entity.NUsers
import com.example.freeti.network_entity.RefreshTokenRequest
import com.example.freeti.network_entity.RegisterRequest
import com.example.freeti.network_entity.TestRequest
import com.example.freeti.network_entity.TestResponse
import com.example.freeti.network_entity.UserResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("auth/username_id")
    suspend fun get_username_id(): Response<UserResponse>

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

    @GET("tasks/tasks")
    suspend fun getTasksForMonth(
        @Query("yearMonth") yearMonth: String
    ): Response<List<NTasks>>

    // Получить задачи за месяц, изменённые после указанного времени
    @GET("tasks/tasks/update")
    suspend fun getTasksForMonthSince(
        @Query("yearMonth") yearMonth: String,
        @Query("since") since: Long   // updated_at > since
    ): Response<List<NTasks>>

    @PATCH("tasks/tasks")
    suspend fun updateTask(
        @Body task: NTasks
    ): Response<NTasks>

    @POST("tasks/tasks")
    suspend fun addTask(
        @Body task: NTasks
    ): Response<NTasks>

    @GET("tasks/othertasks")
    suspend fun getOtherTasksForDay(
        @Query("yearMonth") yearMonth: String,
        @Query("login") login: String
   ): Response<List<NOtherTasks>>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: NUsers
    ): Response<NUsers>

    @GET("users/id")
    suspend fun getUser(
        @Query("id") id: Int
    ): Response<NUsers>

    @GET("users/username")
    suspend fun getUsersSearch(
        @Query("username") username: String
    ): List<NUsers>

    @GET("users/login")
    suspend fun getUsersSearchByLogin(
        @Query("login") login: String
    ): List<NUsers>

    @GET("groups/groups")
    suspend fun getGroups(): List<NGroups>

    @POST("groups/groups")
    suspend fun createGroup(@Body group: NGroups): Response<NGroups>

    // если нужен метод обновления
    @PUT("groups/groups")
    suspend fun updateGroup(@Body group: NGroups): Response<NGroups>
}
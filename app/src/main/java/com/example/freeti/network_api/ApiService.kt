package com.example.freeti.network_api

import com.example.freeti.network_entity.AuthResponse
import com.example.freeti.network_entity.LoginRequest
import com.example.freeti.network_entity.NContacts
import com.example.freeti.network_entity.NGroupEventSearch
import com.example.freeti.network_entity.NGroupEvents
import com.example.freeti.network_entity.NGroups
import com.example.freeti.network_entity.NGroupsUsers
import com.example.freeti.network_entity.NOtherTasks
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

    @POST("tasks/{id}")
    suspend fun addTask(
        @Path("id") taskId: String,
        @Body task: NTasks
    ): Response<NTasks> // TODO необходимо ли

    @GET("othertasks")
    suspend fun getOtherTasksForDay(
        @Query("yearMonth") yearMonth: String,
        @Query("id") since: Int
    ): Response<List<NOtherTasks>>

    @GET("grouptasks")
    suspend fun getGroupTasksForDay(
        @Query("yearMonth") yearMonth: String,
        @Query("id") since: String
    ): Response<List<NGroupEvents>>

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

    @GET("users")
    suspend fun getUsersSearchByLogin(
        @Query("login") login: String
    ): List<NUsers>

    @GET("groups")
    suspend fun getGroups(): List<NGroups>

    @POST("groups")
    suspend fun createGroup(@Body group: NGroups): Response<NGroups>

    // если нужен метод обновления
    @PUT("groups")
    suspend fun updateGroup(@Body group: NGroups): Response<NGroups>

    @PUT("member_switch")
    suspend fun switchMember(
        @Path("user_id") memberId: Int,
        @Path("group_id") groupId: String
    ): Response<NGroupsUsers>

    @PUT("member_delete")
    suspend fun deleteMember(
        @Path("user_id") memberId: Int,
        @Path("group_id") groupId: String
    ): Response<Boolean>

    @PUT("member_delete") // member_leave
    suspend fun leaveGroup(
        @Path("group_id") groupId: String
    ): Response<Boolean>

    @PUT("groups/{group_id}")
    suspend fun deleteGroup(
        @Path("group_id") groupId: String
    ): Response<Boolean>

    @POST("groups/newevent")
    suspend fun addEvent(
        @Path("group_id") groupId: String,
        @Body event: NGroupEventSearch
    ): Response<List<NGroupEvents>>

    @GET("contacts")
    suspend fun getContacts(): List<NContacts>   // или Response<List<NContacts>>

    @GET("users")
    suspend fun getUsersByIds(
        @Query("ids") ids: String
    ): List<NUsers>

    @PUT("groups/delete_event")
    suspend fun deleteGroupEvent(
        @Path("event_id") event_id: String
    ): Response<Boolean>

    @PUT("contacts")
    suspend fun upsertContact(@Body contact: NContacts): Response<NContacts>

    @PUT("contacts/delete")
    suspend fun deleteContact(@Body contact: NContacts): Response<Unit>

    @GET("group_members")
    suspend fun getGroupMembers(@Path("group_id") groupId: String): Response<List<NGroupsUsers>>

    @GET("group_users")
    suspend fun getGroupUsers(@Path("group_id") groupId: String): Response<List<NUsers>>
}
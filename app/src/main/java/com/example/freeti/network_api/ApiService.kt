package com.example.freeti.network_api

import com.example.freeti.data.local.entity.DTasks
import com.example.freeti.network_entity.AuthResponse
import com.example.freeti.network_entity.FinalRegisterRequest
import com.example.freeti.network_entity.LoginRequest
import com.example.freeti.network_entity.NContacts
import com.example.freeti.network_entity.NGroupEventSearch
import com.example.freeti.network_entity.NGroupEvents
import com.example.freeti.network_entity.NGroups
import com.example.freeti.network_entity.NGroupsUsers
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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("tasks/username_id")
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

    @GET("tasks/{id}/repeat")
    suspend fun getRepeatTasks(@Path("id") user_id: Int): List<NRepeatTasks>

    @POST("tasks")
    suspend fun postTasks(@Body tasks: List<DTasks>)

    @POST("tasks")
    suspend fun postTask(@Body tasks: DTasks)

    @POST("test")
    suspend fun send(@Body message : TestRequest): Response<TestResponse>

    @GET("tasks/tasks")
    suspend fun getTasksForMonth(
        @Query("yearMonth") yearMonth: String
    ): Response<List<NTasks>>

    @GET("groups/members_count")
    suspend fun getCountUsersInGroup(@Query("group_id") groupId: String): Response<Int>;

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

    @GET("tasks/unassigned")
    suspend fun getUnassignedTasks(): List<NTasks>

    @GET("tasks/othertasks")
    suspend fun getOtherTasksForDay(
        @Query("yearMonth") yearMonth: String,
        @Query("login") login: String
   ): Response<List<NOtherTasks>>

    @GET("groups/group_tasks")
    suspend fun getGroupTasksForDay(
        @Query("yearMonth") yearMonth: String, //ymd
        @Query("id") groupId: String
    ): Response<List<NGroupEvents>>

    @PUT("users/id")
    suspend fun updateUser(
        @Body user: NUsers
    ): Response<NUsers>

    @GET("users/user")
    suspend fun getUser(): Response<NUsers>

    @GET("users/username")
    suspend fun getUsersSearch(
        @Query("username") username: String
    ): Response<List<NUsers>>

    @GET("users/login")
    suspend fun getUsersSearchByLogin(
        @Query("login") login: String
    ): Response<List<NUsers>>

    @GET("groups/groups")
    suspend fun getGroups(): List<NGroups>

    @POST("groups/groups")
    suspend fun createGroup(@Body group: NGroups): Response<NGroups>

    // если нужен метод обновления
    @PUT("groups/groups")
    suspend fun updateGroup(@Body group: NGroups): Response<NGroups>

    @PUT("groups/member_switch")
    suspend fun switchMember(
        @Query("user_id") memberId: Int,
        @Query("group_id") groupId: String
    ): Response<NGroupsUsers>

    @PUT("groups/member_add")
    suspend fun addMember(
        @Query("user_id") memberId: Int,
        @Query("group_id") groupId: String
    ): Response<Boolean>

    @PUT("groups/member_delete")
    suspend fun deleteMember(
        @Query("user_id") memberId: Int,
        @Query("group_id") groupId: String
    ): Response<Boolean>

    @PUT("groups/leave") // member_leave
    suspend fun leaveGroup(
        @Path("group_id") groupId: String
    ): Response<Boolean>

    @DELETE("groups/delete_group")
    suspend fun deleteGroup(
        @Query("group_id") groupId: String
    ): Response<Boolean>

    @POST("groups/new_event")
    suspend fun addEvent(
        @Query("group_id") groupId: String,
        @Body event: NGroupEventSearch
    ): Response<List<NGroupEvents>>

    @GET("users/contacts")
    suspend fun getContacts(): Response<List<NContacts>>   // или Response<List<NContacts>>

    @GET("users/byIds")
    suspend fun getUsersByIds(
        @Query("ids") ids: String
    ): Response<List<NUsers>>

    @DELETE("groups/delete_event")
    suspend fun deleteGroupEvent(
        @Query("event_id") eventId: String
    ): Response<Boolean>

    @PUT("users/add_contact")
    suspend fun upsertContact(@Body contact: NContacts): Response<NContacts>

    @DELETE("users/delete_contact")
    suspend fun deleteContact(@Body contact: NContacts): Response<Boolean>

    @GET("groups/group_members")
    suspend fun getGroupMembers(@Query("group_id") groupId: String): Response<List<NGroupsUsers>>

    @GET("groups/group_users")
    suspend fun getGroupUsers(@Query("group_id") groupId: String): Response<List<NUsers>>
}
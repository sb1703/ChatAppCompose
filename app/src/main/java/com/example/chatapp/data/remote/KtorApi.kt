package com.example.chatapp.data.remote

import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.ApiResponse
import com.example.chatapp.domain.model.SendMessageDto
import com.example.chatapp.domain.model.UserUpdate
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface KtorApi {

    @POST("/token_verification")
    suspend fun verifyTokenOnBackend(
        @Body request: ApiRequest
    ): ApiResponse

    @POST("/add_chats")
    suspend fun addChats(
        @Body request: ApiRequest
    ): ApiResponse

    @POST("/add_users")
    suspend fun addUsers(
        @Body request: ApiRequest
    ): ApiResponse

    @POST("/fetch_chats")
    suspend fun fetchChats(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30,
        @Body request: ApiRequest
    ): ApiResponse

    @POST("/fetch_last_chat")
    suspend fun fetchLastChat(
        @Body request: ApiRequest
    ): ApiResponse

//    @GET("/fetch_users")
//    suspend fun fetchUsers(
//        @Query("page") page: Int = 1,
//        @Query("limit") limit: Int = 12
//    ): ApiResponse

    @GET("/fetch_users")
    suspend fun fetchUsers(): ApiResponse

    @POST("/search_users")
    suspend fun searchUsers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 12,
        @Body request: ApiRequest
    ): ApiResponse

    @GET("/get_user")
    suspend fun getUserInfo(): ApiResponse

    @POST("/get_user_by_id")
    suspend fun getUserInfoById(
        @Body request: ApiRequest
    ): ApiResponse

    @POST("/get_online_status")
    suspend fun getOnlineStatus(
        @Body request: ApiRequest
    ): ApiResponse

    @POST("/get_last_login")
    suspend fun getLastLogin(
        @Body request: ApiRequest
    ): ApiResponse

    @PUT("/update_user")
    suspend fun updateUser(
        @Body userUpdate: UserUpdate
    ): ApiResponse

    @DELETE("/delete_user")
    suspend fun deleteUser(): ApiResponse

    @GET("/sign_out")
    suspend fun clearSession(): ApiResponse

    @POST("/send_message_notification")
    suspend fun sendMessageNotification(
        @Body body: SendMessageDto
    )

    @POST("/update_fcm_token")
    suspend fun updateFCMToken(
        @Body request: ApiRequest
    ): ApiResponse

}
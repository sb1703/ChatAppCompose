package com.example.chatapp.domain.repository

import androidx.paging.PagingData
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.ApiResponse
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserUpdate
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun saveSignedInState(signedIn: Boolean)
    fun readSignedInState(): Flow<Boolean>
    suspend fun verifyTokenOnBackend(request: ApiRequest): ApiResponse
    suspend fun getUserInfo(): ApiResponse
    suspend fun getUserInfoById(request: ApiRequest): ApiResponse
    suspend fun updateUser(userUpdate: UserUpdate): ApiResponse
    suspend fun deleteUser(): ApiResponse
    suspend fun clearSession(): ApiResponse
    suspend fun addChats(request: ApiRequest): ApiResponse
    suspend fun addUsers(request: ApiRequest): ApiResponse
//    suspend fun fetchChats(request: ApiRequest): Flow<PagingData<Message>>
    suspend fun fetchChats(request: ApiRequest): ApiResponse
    suspend fun fetchLastChat(request: ApiRequest): ApiResponse
//    suspend fun fetchUsers(page: Int,limit: Int): Flow<PagingData<User>>
    suspend fun fetchUsers(): Flow<PagingData<User>>
    suspend fun searchUsers(request: ApiRequest): Flow<PagingData<User>>
}
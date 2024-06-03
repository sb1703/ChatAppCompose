package com.example.chatapp.domain.repository

import androidx.paging.PagingData
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    fun fetchUsers(): Flow<PagingData<User>>
    fun fetchChats(request: ApiRequest): Flow<PagingData<Message>>
    fun searchUsers(request: ApiRequest): Flow<PagingData<User>>
}
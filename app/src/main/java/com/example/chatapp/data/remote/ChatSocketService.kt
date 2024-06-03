package com.example.chatapp.data.remote

import com.example.chatapp.domain.model.Message
import com.example.chatapp.util.RequestState
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(
        senderUserId: String,
        receiver: List<String>
    ): RequestState<Unit>

    suspend fun sendMessage(message: String)

    fun observeMessage(): Flow<Message>

    suspend fun closeSession()

}
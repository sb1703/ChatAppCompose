package com.example.chatapp.data.remote

import com.example.chatapp.domain.model.ChatEvent
import com.example.chatapp.util.RequestState
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(
        senderUserId: String
    ): RequestState<Unit>
    suspend fun sendMessage(message: String, receiverUserIds: List<String>, messageId: String)
    suspend fun sendTyping(receiverUserIds: List<String>)
    suspend fun sendList(receiverUserIds: List<String>)
    suspend fun sendSeen(receiverUserIds: List<String>, messageIds: List<String>, seenAt: String)
    suspend fun sendChatEvent(chatEvent: ChatEvent)
    fun observeChatEvent(): Flow<ChatEvent>
    suspend fun closeSession()

}
package com.example.chatapp.data.remote

import com.example.chatapp.domain.model.ChatEvent
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.Typing
import com.example.chatapp.util.RequestState
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(
        senderUserId: String
    ): RequestState<Unit>
    suspend fun sendMessage(message: String, receiverUserIds: List<String>)
    suspend fun sendTyping(receiverUserIds: List<String>)
    suspend fun sendOnline(online: Boolean)
    suspend fun sendList(receiverUserIds: List<String>)
    suspend fun sendChatEvent(chatEvent: ChatEvent)
    fun observeMessage(): Flow<ChatEvent.MessageEvent>
    fun observeTyping(): Flow<ChatEvent.TypingEvent>
    fun observeOnline(): Flow<ChatEvent.OnlineEvent>
    fun observeList(): Flow<ChatEvent.ListEvent>
    fun observeChatEvent(): Flow<ChatEvent>
    suspend fun closeSession()

}
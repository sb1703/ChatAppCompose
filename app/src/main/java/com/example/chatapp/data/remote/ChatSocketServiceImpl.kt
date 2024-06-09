package com.example.chatapp.data.remote

import android.util.Log
import com.example.chatapp.domain.model.ChatEvent
import com.example.chatapp.util.Constants
import com.example.chatapp.util.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.net.SocketTimeoutException

class ChatSocketServiceImpl(
    private val client: HttpClient
): ChatSocketService {

    private var socket: WebSocketSession? = null

    private val json = Json {
        serializersModule = SerializersModule {
            polymorphic(ChatEvent::class) {
                subclass(ChatEvent.MessageEvent::class)
                subclass(ChatEvent.TypingEvent::class)
                subclass(ChatEvent.OnlineEvent::class)
                subclass(ChatEvent.ListEvent::class)
            }
        }
        classDiscriminator = "type"
    }

    override suspend fun initSession(
        senderUserId: String
    ): RequestState<Unit> {
        return try {
            if(socket?.isActive == true) {
                RequestState.Success(Unit)
            }
            socket = client.webSocketSession {
                url("${Constants.WS_BASE_URL}?userId=$senderUserId")
            }
            if(socket?.isActive == true){
                RequestState.Success(Unit)
            } else{
                RequestState.Error(SocketTimeoutException())
            }
        } catch (e: Exception){
            e.printStackTrace()
            RequestState.Error(SocketTimeoutException())
        }
    }

    override suspend fun sendMessage(message: String, receiverUserIds: List<String>) {
        val chatMessage = ChatEvent.MessageEvent(messageText = message, receiverUserIds = receiverUserIds)
        sendChatEvent(chatMessage)
    }

    override suspend fun sendTyping(receiverUserIds: List<String>) {
        val chatTyping = ChatEvent.TypingEvent(receiverUserIds = receiverUserIds)
        sendChatEvent(chatTyping)
    }

    override suspend fun sendOnline(online: Boolean) {
        val chatOnline = ChatEvent.OnlineEvent(online = online)
        sendChatEvent(chatOnline)
    }

    override suspend fun sendList(receiverUserIds: List<String>) {
        val chatList = ChatEvent.ListEvent(receiverUserIds = receiverUserIds)
        sendChatEvent(chatList)
    }

    override suspend fun sendChatEvent(chatEvent: ChatEvent) {
        val jsonText = json.encodeToString(ChatEvent.serializer(), chatEvent)
        Log.d("json", "JSON - $jsonText")
        try {
            socket?.send(Frame.Text(jsonText))
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun observeChatEvent(coroutineScope: CoroutineScope): SharedFlow<ChatEvent> {
        return try {
            val incomingFlow = socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val jsonText = (it as? Frame.Text)?.readText() ?: ""
                    Log.d("jsonText", "JSON - $jsonText")
                    json.decodeFromString<ChatEvent>(ChatEvent.serializer(), jsonText)
                } ?: flow<ChatEvent> {}

            incomingFlow.shareIn(coroutineScope, SharingStarted.WhileSubscribed(), replay = 1)
        } catch (e: Exception) {
            e.printStackTrace()
            flow<ChatEvent> {}.shareIn(coroutineScope, SharingStarted.WhileSubscribed(), replay = 1)
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }

}
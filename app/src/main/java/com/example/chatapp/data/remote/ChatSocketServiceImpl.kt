package com.example.chatapp.data.remote

import com.example.chatapp.domain.model.Message
import com.example.chatapp.util.Constants
import com.example.chatapp.util.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import java.net.SocketTimeoutException

class ChatSocketServiceImpl(
    private val client: HttpClient
): ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(
        senderUserId: String,
        receiver: List<String>
    ): RequestState<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${Constants.WS_BASE_URL}?userId=$senderUserId&receiver=${receiver.joinToString(",")}")
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

    override suspend fun sendMessage(message: String) {
        try {
            socket?.send(Frame.Text(message))
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun observeMessage(): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val message = Json.decodeFromString<Message>(json)
                    message
                } ?: flow {  }
        } catch (e: Exception){
            e.printStackTrace()
            flow{  }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }

}
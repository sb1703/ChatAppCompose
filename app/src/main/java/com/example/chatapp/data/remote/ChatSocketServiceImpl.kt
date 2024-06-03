package com.example.chatapp.data.remote

import android.util.Log
import androidx.core.app.PendingIntentCompat.send
import com.example.chatapp.domain.model.Message
import com.example.chatapp.util.Constants
import com.example.chatapp.util.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import java.net.SocketTimeoutException
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

class ChatSocketServiceImpl(
    private val client: HttpClient
): ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(
        senderUserId: String,
        receiver: List<String>
    ): RequestState<Unit> {
        return try {
            Log.d("debugging","senderUserId: $senderUserId && receiver: $receiver && joinToString: ${receiver.joinToString(",")}")
            socket = client.webSocketSession {
                url("${Constants.WS_BASE_URL}?userId=$senderUserId&receiver=${receiver.joinToString(",")}")
            }
            if(socket?.isActive == true){
                Log.d("debugging","requestStateSuccessIsActive")
                RequestState.Success(Unit)
            } else{
                Log.d("debugging","requestStateErrorSocketTimeOutException")
                RequestState.Error(SocketTimeoutException())
            }
        } catch (e: Exception){
            Log.d("debugging","exceptionCatch")
            e.printStackTrace()
            RequestState.Error(SocketTimeoutException())
        }
    }

    override suspend fun sendMessage(message: String) {
        try {
            Log.d("debugging2","sendMessageFrame: $message")
            socket?.send(Frame.Text(message))
        } catch (e: Exception){
            Log.d("debugging","sendMessageFrameError")
            e.printStackTrace()
        }
    }

    override fun observeMessage(): Flow<Message> {
        return try {
            Log.d("debugging","observingMessageFrame")
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val message = Json.decodeFromString<Message>(json)
                    message
                } ?: flow {  }
        } catch (e: Exception){
            Log.d("debugging","observeMessageFrameError")
            e.printStackTrace()
            flow{  }
        }
    }

    override suspend fun closeSession() {
        Log.d("debugging","closeSession")
        socket?.close()
    }

}
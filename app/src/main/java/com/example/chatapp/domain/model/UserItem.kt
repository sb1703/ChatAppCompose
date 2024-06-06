package com.example.chatapp.domain.model

import io.ktor.http.cio.websocket.WebSocketSession
import kotlinx.coroutines.Job

data class UserItem(
    val id: String = "",
    val userId: String? = null,
    val name: String = "",
    val emailAddress: String = "",
    val profilePhoto: String = "",
    val list: List<String> = emptyList(),
    val online: Boolean = false,
    val lastLogin: String? = null,
    val socket: WebSocketSession? = null,
    val isTyping: Boolean = false,
    val lastMessage: Message? = null,
    val typingJob: Job? = null
)

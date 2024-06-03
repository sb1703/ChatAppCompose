package com.example.chatapp.domain.model

import io.ktor.http.cio.websocket.WebSocketSession
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class User(
    val id: String = "",
    val userId: String? = null,
    val name: String = "",
    val emailAddress: String = "",
    val profilePhoto: String = "",
//    val list: List<User> = emptyList(),
    val list: List<String> = emptyList(),
    val online: Boolean = false,
    val lastLogin: String? = null,
    val socket: WebSocketSession? = null
)

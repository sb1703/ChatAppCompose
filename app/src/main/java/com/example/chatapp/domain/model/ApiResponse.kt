package com.example.chatapp.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ApiResponse(
    val success: Boolean,
    val user: User? = null,
    val chat: Message? = null,
    val message: String? = null,
    val prevPage: Int? = null,
    val nextPage: Int? = null,
    val listMessages: List<Message> = emptyList(),
    val listUsers: List<User> = emptyList(),
    val online: Boolean = false,
    val lastLogin: String? = null,
    val messageId: String = "",
    @Transient
    val error: Exception? = null
)

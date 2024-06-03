package com.example.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val conversationId: String?,
    val member: List<String> = emptyList(),
    val messages: List<Message> = emptyList()
)

package com.example.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageDto(
    val to: String,
    val notification: NotificationBody
)

@Serializable
data class NotificationBody(
    val title: String,
    val body: String
)
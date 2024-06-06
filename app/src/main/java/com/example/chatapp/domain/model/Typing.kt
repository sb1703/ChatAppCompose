package com.example.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Typing(
    val author: String? = null,
    val receiver: List<String?> = emptyList(),
    val typingText: String? = null,
)

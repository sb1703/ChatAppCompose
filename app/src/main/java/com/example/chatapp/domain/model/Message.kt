package com.example.chatapp.domain.model

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Serializable
data class Message(
    val messageId: String? = null,
    val author: String? = null,
    val receiver: List<String?> = emptyList(),
    val seenBy: List<SeenBy> = emptyList(),
    val messageText: String? = null,
    val time: String = getCurrentTimeIn12HourFormat()
)

@Serializable
data class SeenBy(
    val userId: String,
    val seenAt: String
)

fun getCurrentTimeIn12HourFormat(): String {
    val currentTime = Calendar.getInstance().time
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(currentTime)
}
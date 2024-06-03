package com.example.chatapp.domain.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Serializable
data class Message(
    val messageId: String? = null,
    val author: String? = null,
    val receiver: List<String?> = emptyList(),
    val messageText: String? = null,
    val time: String = getCurrentTimeIn12HourFormat()
)

fun getCurrentTimeIn12HourFormat(): String {
    val currentTime = Calendar.getInstance().time
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(currentTime)
}
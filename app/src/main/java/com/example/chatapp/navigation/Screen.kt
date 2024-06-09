package com.example.chatapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.chatapp.util.Constants.CHAT_USER_ID

sealed class Screen(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    data object Login: Screen(route = "login_screen")
    data object Main: Screen(
        route = "main_screen",
        title = "Chats",
        icon = Icons.Filled.ChatBubble
    )
    data object Chat: Screen(route = "chat_screen")
    data object Profile: Screen(
        route = "profile_screen",
        title = "Profile",
        icon = Icons.Filled.AccountCircle
    )
    data object MainChat: Screen(
        route = "main_chat_screen"
    )
}
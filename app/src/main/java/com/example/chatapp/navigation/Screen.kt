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
    data object Chat: Screen(route = "chat_screen?$CHAT_USER_ID={$CHAT_USER_ID}") {
        fun passId(id: String) = "chat_screen?$CHAT_USER_ID=$id"
    }
    data object Profile: Screen(
        route = "profile_screen",
        title = "Profile",
        icon = Icons.Filled.AccountCircle
    )
}
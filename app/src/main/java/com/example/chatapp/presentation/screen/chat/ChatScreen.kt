package com.example.chatapp.presentation.screen.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.example.chatapp.presentation.screen.common.MainChatViewModel

@Composable
fun ChatScreen(
    navController: NavHostController,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: MainChatViewModel,
    chatUser: User,
    online: Boolean,
    lastLogin: String,
    chats: List<Message>
) {

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if(event == Lifecycle.Event.ON_DESTROY) {
                viewModel.disconnect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val currentUser by viewModel.currentUser.collectAsState()
    val chatText by viewModel.chatText.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()

    Scaffold(
        topBar = {
            ChatTopBar(
                onBackStackClicked = {
                    navController.popBackStack()
                },
                name = chatUser.name,
                profilePicture = chatUser.profilePhoto,
                online = online,
                isTyping = isTyping,
                lastLogin = lastLogin
            )
        },
        content = { paddingValue ->
            Surface(
                modifier = Modifier.padding(paddingValue)
            ) {
                ChatContent(
                    chats = chats,
                    currentUser = currentUser,
                    chatUser = chatUser,
                    currentUserId = currentUser.userId.toString()
                )
            }
        },
        bottomBar = {
            ChatBottomBar(
                text = chatText,
                onTextChange = { viewModel.updateChatText(it) },
                onSendClicked = {
                    viewModel.sendMessage(currentUser)
                    viewModel.clearChatText()
                }
            )
        }
    )

}
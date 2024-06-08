package com.example.chatapp.presentation.screen.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.chatapp.presentation.screen.main.MainViewModel

@Composable
fun ChatScreen(
    navController: NavHostController,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    chatViewModel: ChatViewModel = hiltViewModel()
) {

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if(event == Lifecycle.Event.ON_DESTROY) {
                chatViewModel.setOnlineFalse()
                chatViewModel.disconnect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val currentUser by chatViewModel.currentUser.collectAsState()
    val chatUser by chatViewModel.chatUser.collectAsState()
    val chatText by chatViewModel.chatText.collectAsState()
    val chats by chatViewModel.fetchedChat.collectAsState()
    val online by chatViewModel.online.collectAsState()

    Scaffold(
        topBar = {
            ChatTopBar(
                onBackStackClicked = {
                    navController.popBackStack()
//                    chatViewModel.disconnect()
                },
                name = chatUser.name,
                profilePicture = chatUser.profilePhoto,
                online = online
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
                onTextChange = { chatViewModel.updateChatText(it) },
                onSendClicked = {
                    chatViewModel.sendMessage(currentUser)
                    chatViewModel.clearChatText()
                }
            )
        }
    )

}
package com.example.chatapp.presentation.screen.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.chatapp.presentation.screen.main.MainViewModel

@Composable
fun ChatScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    chatViewModel: ChatViewModel = hiltViewModel()
) {

    val currentUser by mainViewModel.currentUser.collectAsState()

    LaunchedEffect(key1 = true) {
        chatViewModel.getChatIdArgument()
        chatViewModel.getUserInfoByUserId()
        chatViewModel.fetchChats()
        chatViewModel.connectToChat(currentUser)
    }

    val chatUser by chatViewModel.chatUser.collectAsState()
    val chatText by chatViewModel.chatText.collectAsState()
    val chats by chatViewModel.fetchedChat.collectAsState()

    Scaffold(
        topBar = {
            ChatTopBar(
                onBackStackClicked = {
                    navController.popBackStack()
                    chatViewModel.disconnect()
                },
                name = chatUser.name,
                profilePicture = chatUser.profilePhoto,
                online = chatUser.online
            )
        },
        content = { paddingValue ->
            Surface(
                modifier = Modifier.padding(paddingValue)
            ) {
                currentUser?.let { ChatContent(
                    chats = chats,
                    currentUser = it,
                    chatUser = chatUser,
                    currentUserId = it.userId.toString()
                ) }
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
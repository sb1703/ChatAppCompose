package com.example.chatapp.presentation.screen.chat

import android.util.Log
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
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chatapp.presentation.screen.main.MainViewModel
import kotlinx.coroutines.Dispatchers

@Composable
fun ChatScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    chatViewModel: ChatViewModel = hiltViewModel()
) {

    val currentUser by mainViewModel.currentUser.collectAsState()

    LaunchedEffect(key1 = true) {
        chatViewModel.getChatIdArgument()
//        chatViewModel.getCurrentUser(c)
        chatViewModel.getUserInfoByUserId()
        chatViewModel.fetchChats()
        chatViewModel.connectToChat(currentUser)
    }

//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(key1 = lifecycleOwner) {
//        val observer = LifecycleEventObserver { _ , event ->
//            if(event == Lifecycle.Event.ON_START){
//                chatViewModel.connectToChat()
//            } else if(event == Lifecycle.Event.ON_STOP){
//                chatViewModel.disconnect()
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }

//    val currentUser by mainViewModel.currentUser.collectAsState()
    val chatUser by chatViewModel.chatUser.collectAsState()
//    val chats = chatViewModel.fetchedChat.collectAsLazyPagingItems()
    val chatText by chatViewModel.chatText.collectAsState()
    val chats by chatViewModel.fetchedChat.collectAsState()

    Log.d("debugging","ChatScreenCurrentUser: ${currentUser?.userId}")

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
                    ?: Log.d("debugging","currentUser is null")
            }
        },
        bottomBar = {
            ChatBottomBar(
                text = chatText,
                onTextChange = { chatViewModel.updateChatText(it) },
                onSendClicked = {
//                    mainViewModel.currentUser.value?.userId?.let { chatViewModel.addChat(it) }
                    chatViewModel.sendMessage(currentUser)
                    chatViewModel.clearChatText()
                }
            )
        }
    )

}
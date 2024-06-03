package com.example.chatapp.presentation.screen.main

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chatapp.navigation.Screen
import com.example.chatapp.presentation.screen.common.BottomBar

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    Log.d("debugging","MainScreen")
    LaunchedEffect(key1 = true) {
        Log.d("debugging","MainScreenLaunched")
        mainViewModel.getCurrentUser()
        mainViewModel.fetchUsers()
        Log.d("debugging","setOnlineTrueLaunchedEffect")
        mainViewModel.setOnlineTrue()
        Log.d("debugging","setOnlineTrueLaunchedEffectDone!")
        Log.d("debugging","MainScreenLaunchedDone!")
    }

//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(key1 = lifecycleOwner) {
//        val observer = LifecycleEventObserver { _ , event ->
//            if(event == Lifecycle.Event.ON_START){
//                mainViewModel.setOnlineTrue()
//            } else if(event == Lifecycle.Event.ON_STOP){
//                mainViewModel.setOnlineFalse()
//            }
//        }
//        lifecycleOwner.lifecycle.addObserver(observer)
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }


    val users = mainViewModel.fetchedUser.collectAsLazyPagingItems()
    val searchedUsers = mainViewModel.searchedUser.collectAsLazyPagingItems()
    val currentUser = mainViewModel.currentUser.collectAsState()
    val searchQuery = mainViewModel.searchQuery.collectAsState()

    Log.d("debugging","MainScreenCurrentUser: ${currentUser.value.userId}")

    Scaffold(
        topBar = {
            MainTopBar(
                text = searchQuery.value,
                onTextChange = {
                    mainViewModel.updateSearchQuery(it)
                    mainViewModel.searchUser()
                },
                onSearchClicked = {
                    mainViewModel.searchUser()
                },
                currentUser = currentUser.value
            )
        },
        content = { paddingValue ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue)
            ) {
                MainContent(
                    users = users,
                    searchedUsers = searchedUsers,
                    searchQuery = searchQuery.value,
                    navigationToChatScreen = {
                        navController.navigate(Screen.Chat.passId(id = it))
                    },
                    fetchLastMessage = { userId ->
                        mainViewModel.updateChatId(userId)
                        mainViewModel.getUserInfoByUserId()
                        mainViewModel.fetchLastChat(userId)
                        Log.d("lastMessageDebug","fetchLastMessage")
                        Log.d("lastMessageDebug", mainViewModel.lastMessage.value?.messageText.toString())
                        mainViewModel.lastMessage.value
                    },
                    getAuthorName = { authorUserId ->
                        if(authorUserId == currentUser.value.userId) {
                            "You"
                        } else {
                            mainViewModel.chatUser.value.name
                        }
//                        mainViewModel.chatUser.value.name
                    }
                )
            }
        },
        bottomBar = {
            BottomBar(navController = navController)
        }
    )
}
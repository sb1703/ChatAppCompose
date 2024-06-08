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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chatapp.navigation.Screen
import com.example.chatapp.presentation.screen.common.BottomBar

@Composable
fun MainScreen(
    navController: NavHostController,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    mainViewModel: MainViewModel = hiltViewModel()
) {

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if(event == Lifecycle.Event.ON_DESTROY) {
                mainViewModel.setOnlineFalse()
                mainViewModel.disconnect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val currentUser = mainViewModel.currentUser.collectAsState()
    val users = mainViewModel.fetchedUser.collectAsState()
    val searchedUsers = mainViewModel.searchedUser.collectAsLazyPagingItems()
    val searchQuery = mainViewModel.searchQuery.collectAsState()

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
                    users = users.value,
                    searchedUsers = searchedUsers,
                    searchQuery = searchQuery.value,
                    navigationToChatScreen = {
                        Log.d("disconnect", "disconnecting main navigation to chat screen")
                        mainViewModel.disconnect()
                        navController.navigate(Screen.Chat.passId(id = it))
                    },
                    getAuthorName = { authorUserId ->
                        if(authorUserId == currentUser.value.userId) {
                            "You"
                        } else {
                            users.value.find { it.userId == authorUserId }?.name ?: ""
                        }
                    }
                )
            }
        },
        bottomBar = {
            BottomBar(
                navController = navController
            )
        }
    )
}
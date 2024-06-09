package com.example.chatapp.presentation.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chatapp.navigation.Screen
import com.example.chatapp.presentation.screen.common.BottomBar
import com.example.chatapp.presentation.screen.common.MainChatViewModel

@Composable
fun MainScreen(
    navController: NavHostController,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: MainChatViewModel
) {

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if(event == Lifecycle.Event.ON_DESTROY) {
                viewModel.setOnlineFalse()
                viewModel.disconnect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val currentUser = viewModel.currentUser.collectAsState()
    val users = viewModel.fetchedUser.collectAsState()
    val searchedUsers = viewModel.searchedUser.collectAsLazyPagingItems()
    val searchQuery = viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            MainTopBar(
                text = searchQuery.value,
                onTextChange = {
                    viewModel.updateSearchQuery(it)
                    viewModel.searchUser()
                },
                onSearchClicked = {
                    viewModel.searchUser()
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
                        viewModel.updateChatId(it)
                        navController.navigate(Screen.Chat.route)
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
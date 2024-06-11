package com.example.chatapp.presentation.screen.main

import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserItem
import com.example.chatapp.navigation.Screen
import com.example.chatapp.presentation.screen.common.BottomBar
import com.example.chatapp.presentation.screen.common.MainChatViewModel

@Composable
fun MainScreen(
    navController: NavHostController,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: MainChatViewModel,
    currentUser: User,
    users: List<UserItem>
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

    val searchedUsers = viewModel.searchedUser.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            MainTopBar(
                text = searchQuery,
                onTextChange = {
                    viewModel.updateSearchQuery(it)
                    viewModel.searchUser()
                },
                onSearchClicked = {
                    viewModel.searchUser()
                },
                currentUser = currentUser
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
                    searchQuery = searchQuery,
                    navigationToChatScreen = {
                        viewModel.updateChatId(it)
                        navController.navigate(Screen.Chat.route)
                    },
                    getAuthorName = { authorUserId ->
                        if(authorUserId == currentUser.userId) {
                            "You"
                        } else {
                            users.find { it.userId == authorUserId }?.name ?: ""
                        }
                    },
                    currentUserId = currentUser.userId ?: "",
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
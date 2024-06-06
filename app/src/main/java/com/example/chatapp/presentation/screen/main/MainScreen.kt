package com.example.chatapp.presentation.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.chatapp.navigation.Screen
import com.example.chatapp.presentation.screen.common.BottomBar

@Composable
fun MainScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {

    LaunchedEffect(key1 = true) {
        mainViewModel.getCurrentUser()
        mainViewModel.fetchUsers()
    }

    val currentUser = mainViewModel.currentUser.collectAsState()
    LaunchedEffect(key1 = currentUser.value.userId) {
        if (currentUser.value.userId != null) {
            mainViewModel.connectToChat()
        }
    }

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
            BottomBar(navController = navController)
        }
    )
}
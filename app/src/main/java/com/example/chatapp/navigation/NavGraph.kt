package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatapp.connectivity.ConnectivityObserver
import com.example.chatapp.presentation.screen.chat.ChatScreen
import com.example.chatapp.presentation.screen.common.MainChatViewModel
import com.example.chatapp.presentation.screen.login.LoginScreen
import com.example.chatapp.presentation.screen.main.MainScreen
import com.example.chatapp.presentation.screen.profile.ProfileScreen
import com.example.chatapp.presentation.screen.profile.ProfileViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                navController = navController
            )
        }
        composable(route = Screen.Profile.route) {
            val profileViewModel = hiltViewModel<ProfileViewModel>()
            val network by profileViewModel.network.collectAsState()
            val currentUser by profileViewModel.currentUser.collectAsState()
            LaunchedEffect(key1 = network, key2 = currentUser) {
                if (network == ConnectivityObserver.Status.Available) {
                    profileViewModel.getCurrentUser()
                    if(currentUser.userId != null) {
                        profileViewModel.updateName(currentUser.name)
                    }
                }
            }
            ProfileScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                currentUser = currentUser
            )
        }
        navigation(
            route = Screen.MainChat.route,
            startDestination = Screen.Main.route
        ) {
            composable(route = Screen.Main.route) { entry ->
                val viewModel = entry.sharedViewModel(navController)
                val network by viewModel.network.collectAsState()
                val currentUser by viewModel.currentUser.collectAsState()
                val fetchedUser by viewModel.fetchedUser.collectAsState()
                val isFCMTokenSentToServer by viewModel.isFCMTokenSentToServer.collectAsState()
                LaunchedEffect(key1 = network, key2 = currentUser) {
                    if (network == ConnectivityObserver.Status.Available) {
                        if(!isFCMTokenSentToServer) {
                            viewModel.updateFCMTokenServer()
                            viewModel.setIsFCMTokenSentToServerToTrue()
                        }
                        if(currentUser.userId == null) {
                            viewModel.getCurrentUser()
                        } else {
                            viewModel.connectToChat()
                            viewModel.fetchUsers()
                        }
                    } else {
                        viewModel.disconnect()
                    }
                }
                MainScreen(
                    navController = navController,
                    viewModel = viewModel,
                    currentUser = currentUser,
                    users = fetchedUser
                )
            }
            composable(
                route = Screen.Chat.route
            ) { entry ->
                val viewModel = entry.sharedViewModel(navController)
                val network by viewModel.network.collectAsState()
                val chatId by viewModel.chatId.collectAsState()
                val chatUser by viewModel.chatUser.collectAsState()
                val online by viewModel.online.collectAsState()
                val lastLogin by viewModel.lastLogin.collectAsState()
                val chats by viewModel.fetchedChat.collectAsState()
                LaunchedEffect(key1 = network) {
                    if (network == ConnectivityObserver.Status.Available) {
                        viewModel.connectToChat()
                        if (chatId.isNotBlank()) {
                            viewModel.getUserInfoByUserId()
                            viewModel.fetchChats()
                            viewModel.getOnlineStatus()
                        }
                    } else {
                        viewModel.disconnect()
                    }
                }
                LaunchedEffect(key1 = online) {
                    if (network == ConnectivityObserver.Status.Available) {
                        if(!online) {
                            viewModel.getLastLogin()
                        }
                    }
                }
                LaunchedEffect(key1 = chats.size) {
                    if (network == ConnectivityObserver.Status.Available) {
                        viewModel.sendSeen()
                    }
                }
                ChatScreen(
                    navController = navController,
                    viewModel = viewModel,
                    chatUser = chatUser,
                    online = online,
                    lastLogin = lastLogin,
                    chats = chats
                )
            }
        }
    }
}

@Composable
fun NavBackStackEntry.sharedViewModel(
    navController: NavHostController
): MainChatViewModel {
    val NavGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(NavGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
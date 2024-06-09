package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.chatapp.presentation.screen.chat.ChatScreen
import com.example.chatapp.presentation.screen.common.MainChatViewModel
import com.example.chatapp.presentation.screen.login.LoginScreen
import com.example.chatapp.presentation.screen.main.MainScreen
import com.example.chatapp.presentation.screen.profile.ProfileScreen

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
            ProfileScreen(
                navController = navController
            )
        }
        navigation(
            route = Screen.MainChat.route,
            startDestination = Screen.Main.route
        ) {
            composable(route = Screen.Main.route) { entry ->
                val viewModel = entry.sharedViewModel(navController)
                viewModel.getCurrentUser()
                MainScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
            composable(
                route = Screen.Chat.route
            ) { entry ->
                val viewModel = entry.sharedViewModel(navController)
                ChatScreen(
                    navController = navController,
                    viewModel = viewModel
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
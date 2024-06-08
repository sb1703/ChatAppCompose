package com.example.chatapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.chatapp.presentation.screen.chat.ChatScreen
import com.example.chatapp.presentation.screen.chat.ChatViewModel
import com.example.chatapp.presentation.screen.login.LoginScreen
import com.example.chatapp.presentation.screen.main.MainScreen
import com.example.chatapp.presentation.screen.main.MainViewModel
import com.example.chatapp.presentation.screen.profile.ProfileScreen
import com.example.chatapp.util.Constants.CHAT_USER_ID

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
        composable(route = Screen.Main.route) {
            MainScreen(
                navController = navController
            )
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                navController = navController
            )
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument(name = CHAT_USER_ID){
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            ChatScreen(
                navController = navController
            )
        }
    }
}
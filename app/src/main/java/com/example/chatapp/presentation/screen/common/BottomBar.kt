package com.example.chatapp.presentation.screen.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.chatapp.navigation.Screen

@Composable
fun BottomBar(
    navController: NavHostController
) {
    val screens = listOf(
        Screen.Main,
        Screen.Profile
    )
//    list of all the screens

    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    we will observe this navBackStackEntry whenever it's value is changed then we will be notified about that

    val currentDestination = navBackStackEntry?.destination
//    we get current destination and also this is nullable type

    NavigationBar {
        screens.forEach { screen ->
            AddItems(screen = screen, currentDestination = currentDestination, navController = navController)
        }
    }
}

@Composable
fun RowScope.AddItems(
    screen: Screen,
    currentDestination: NavDestination?,
    navController: NavHostController
){
    NavigationBarItem(
        label = {
            screen.title?.let {
                Text(
                    text = it
                )
            }
        },
        icon = {
            screen.icon?.let { Icon(
                imageVector = it,
                contentDescription = "Navigation Icon"
            ) }
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        colors = NavigationBarItemDefaults.colors(
//            selectedIconColor = Color.Black,
            unselectedIconColor = LocalContentColor.current.copy(alpha = 0.38f),
//            selectedTextColor = Color.White,
            unselectedTextColor = LocalContentColor.current.copy(alpha = 0.38f),
//            indicatorColor = Color.White
        ),
        onClick = {
            navController.navigate(screen.route){
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
//                This will allow us to avoid the multiple copies of same destination when reselecting the same item
            }
        }
    )
}
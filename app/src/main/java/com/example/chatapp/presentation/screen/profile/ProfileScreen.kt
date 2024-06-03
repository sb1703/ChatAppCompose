package com.example.chatapp.presentation.screen.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.chatapp.presentation.screen.common.BottomBar
import com.example.chatapp.presentation.screen.main.MainViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    mainViewModel: MainViewModel
) {

    val nameQuery = profileViewModel.nameQuery.collectAsState()
    val currentUser = mainViewModel.currentUser.collectAsState()
    val isReadOnly = profileViewModel.isReadOnly.collectAsState()

    LaunchedEffect(key1 = true) {
        profileViewModel.updateName(currentUser.value.name)
    }

    Scaffold(
        topBar = {
            ProfileTopBar()
        },
        content = { paddingValue ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue)
            ) {
                ProfileContent(
                    name = nameQuery.value,
                    mail = currentUser.value.emailAddress,
                    onNameChange = {
                        profileViewModel.updateName(it)
                    },
                    isReadOnly = isReadOnly.value,
                    onEditClicked = {
                        profileViewModel.toggleIsReadOnly()
                        profileViewModel.updateUser()
                    },
                    profilePicture = currentUser.value.profilePhoto
                )
            }
        },
        bottomBar = {
            BottomBar(navController = navController)
        }
    )

}
package com.example.chatapp.presentation.screen.profile

import android.util.Log
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

//    LaunchedEffect(key1 = true) {
//        Log.d("profileDebug","getCurrentUser()")
//        profileViewModel.getCurrentUser()
//    }

    val nameQuery = profileViewModel.nameQuery.collectAsState()
    val currentUser = mainViewModel.currentUser.collectAsState()
    val isReadOnly = profileViewModel.isReadOnly.collectAsState()

    LaunchedEffect(key1 = true) {
        Log.d("profileDebug","updateName()")
        profileViewModel.updateName(currentUser.value.name)
        Log.d("profileDebug",currentUser.value.name)
        Log.d("profileDebug",nameQuery.value)
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
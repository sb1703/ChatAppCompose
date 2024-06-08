package com.example.chatapp.presentation.screen.profile

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
import com.example.chatapp.presentation.screen.common.BottomBar
import com.example.chatapp.presentation.screen.main.MainViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if(event == Lifecycle.Event.ON_DESTROY) {
                profileViewModel.setOnlineFalse()
                profileViewModel.disconnect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val nameQuery = profileViewModel.nameQuery.collectAsState()
    val isReadOnly = profileViewModel.isReadOnly.collectAsState()
    val currentUser = profileViewModel.currentUser.collectAsState()

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
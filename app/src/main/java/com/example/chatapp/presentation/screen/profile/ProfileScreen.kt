package com.example.chatapp.presentation.screen.profile

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.chatapp.domain.model.User
import com.example.chatapp.presentation.screen.common.BottomBar

@Composable
fun ProfileScreen(
    navController: NavHostController,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    currentUser: User
) {

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if(event == Lifecycle.Event.ON_DESTROY) {
                profileViewModel.disconnect()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val nameQuery by profileViewModel.nameQuery.collectAsState()
    val isReadOnly by profileViewModel.isReadOnly.collectAsState()

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
                    name = nameQuery,
                    mail = currentUser.emailAddress,
                    onNameChange = {
                        profileViewModel.updateName(it)
                    },
                    isReadOnly = isReadOnly,
                    onEditClicked = {
                        profileViewModel.toggleIsReadOnly()
                        profileViewModel.updateUser()
                    },
                    profilePicture = currentUser.profilePhoto
                )
            }
        },
        bottomBar = {
            BottomBar(navController = navController)
        }
    )

}
package com.example.chatapp.presentation.screen.login

import android.app.Activity
import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.ApiResponse
import com.example.chatapp.navigation.Screen
import com.example.chatapp.presentation.screen.common.StartActivityForResult
import com.example.chatapp.presentation.screen.common.signIn
import com.example.chatapp.util.RequestState

@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {

    val signedInState by loginViewModel.signedInState
    val messageBarState by loginViewModel.messageBarState
    val apiResponse by loginViewModel.apiResponse

    Scaffold(
        topBar = {
            LoginTopBar()
        },
        content = { paddingValue ->
            LoginContent(
                signedInState = signedInState,
                messageBarState = messageBarState,
                onButtonClicked = {
                    loginViewModel.saveSignedInState(signedIn = true)
                },
                paddingValue = paddingValue
            )
        }
    )

    val activity = LocalContext.current as Activity

    StartActivityForResult(
        key = signedInState,
        onResultReceived = { tokenId ->
            Log.d("tokenId",tokenId)
            Log.d("debug","loginscreen")
            loginViewModel.verifyTokenOnBackend(
                request = ApiRequest(tokenId = tokenId)
            )
        },
        onDialogDismissed = {
            loginViewModel.saveSignedInState(signedIn = false)
        }
    ) { activityLauncher ->
        if (signedInState) {
            signIn(
                activity = activity,
                launchActivityResult = { intentSenderRequest ->
                    activityLauncher.launch(intentSenderRequest)
                },
                accountNotFound = {
                    loginViewModel.saveSignedInState(signedIn = false)
                    loginViewModel.updateMessageBarState()
                }
            )
        }
    }

    LaunchedEffect(key1 = apiResponse) {
        when (apiResponse) {
            is RequestState.Success -> {
                Log.d("debug","apiResponse")
                val response = (apiResponse as RequestState.Success<ApiResponse>).data.success
                if (response) {
                    navigateToMainScreen(navController = navController)
                } else {
                    loginViewModel.saveSignedInState(signedIn = false)
                }
            }
            else -> {
                Log.d("debug","apiResponse-error")
            }
        }
    }

}

private fun navigateToMainScreen(
    navController: NavHostController
) {
    navController.navigate(route = Screen.Main.route) {
        popUpTo(route = Screen.Login.route) {
            inclusive = true
        }
    }
}
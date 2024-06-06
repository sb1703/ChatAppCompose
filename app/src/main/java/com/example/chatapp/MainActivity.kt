package com.example.chatapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.navigation.SetupNavGraph
import com.example.chatapp.presentation.screen.main.MainViewModel
import com.example.chatapp.ui.theme.ChatAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private var isLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                val currentUser by mainViewModel.currentUser.collectAsState()
                LaunchedEffect(key1 = currentUser) {
                    isLoggedIn = currentUser != null
                }
                val navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    mainViewModel = mainViewModel,
//                    destroyCalled = {
//                        onDestroy()
//                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isLoggedIn) {
            Log.d("debugging2","Destroying & settingOnlineFalse")
            lifecycleScope.launch {
                mainViewModel.setOnlineFalse()
                mainViewModel.disconnect()
            }
        } else {
            Log.d("debugging2","Destroying")
        }
    }
}
package com.example.chatapp.presentation.screen.login

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.chatapp.ui.theme.Gray500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Sign in",
                color = if (!isSystemInDarkTheme()) Color.White else Color.LightGray
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (!isSystemInDarkTheme()) Gray500 else Color.Black
        )
    )
}

@Composable
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun LoginTopBarPreview() {
    LoginTopBar()
}
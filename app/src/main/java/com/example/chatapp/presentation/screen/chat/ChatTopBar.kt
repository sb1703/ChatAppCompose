package com.example.chatapp.presentation.screen.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.chatapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    onBackStackClicked: () -> Unit,
    name: String,
    profilePicture: String,
    online: Boolean,
    isTyping: Boolean,
    lastLogin: String
) {
    TopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                            append(name)
                        }
                    },
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if(isTyping) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraLight)) {
                                append("typing...")
                            }
                        },
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            Row(
                modifier = Modifier.padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                IconButton(
                    onClick = {
                        onBackStackClicked()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "BackStack Icon"
                    )
                }
                AsyncImage(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(data = profilePicture)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .build(),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop
                )
            }
        },
        actions = {
            AnimatedVisibility(visible = online) {
                Surface(
                    modifier = Modifier
                        .padding(end = 7.dp)
                        .size(10.dp)
                        .clip(CircleShape),
                    color = Color.Red
                ) {}
            }
            AnimatedVisibility(visible = !online) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                            append("Last Login: \n " + lastLogin)
                        }
                    },
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}

@Preview
@Composable
private fun ChatTopBarPreview() {
    ChatTopBar(
        onBackStackClicked = {  },
        name = "John Doe",
        profilePicture = "",
        online = false,
        isTyping = true,
        lastLogin = "12:00 AM"
    )
}
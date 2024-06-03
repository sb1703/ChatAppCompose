package com.example.chatapp.presentation.screen.main

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.chatapp.R
import com.example.chatapp.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    currentUser: User?
) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Box(
        modifier = Modifier.height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        CenterAlignedTopAppBar(
            modifier = Modifier.fillMaxWidth().nestedScroll(scrollBehavior.nestedScrollConnection),
            scrollBehavior = scrollBehavior,
            title = {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .height(64.dp),
                    shape = RoundedCornerShape(30.dp),
                    value = text,
                    onValueChange = { onTextChange(it) },
                    placeholder = {
                        Text(
                            modifier = Modifier
                                .alpha(0.38f)
                                .fillMaxWidth(),
                            text = "Search Here...",
                            color = Color.Black
                        )
                    },
                    singleLine = true,
                    leadingIcon = {
                        IconButton(
                            onClick = {  }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search Icon",
                                tint = if(isSystemInDarkTheme()) Color.White.copy(alpha = 0.38f) else Color.Black.copy(alpha = 0.38f)
                            )
                        }
                    },
                    suffix = {
                        if (currentUser != null) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(data = currentUser.profilePhoto)
                                    .placeholder(R.drawable.ic_placeholder)
                                    .error(R.drawable.ic_placeholder)
                                    .build(),
                                contentDescription = "image",
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(data = R.drawable.ic_placeholder)
                                    .placeholder(R.drawable.ic_placeholder)
                                    .error(R.drawable.ic_placeholder)
                                    .build(),
                                contentDescription = "image",
                                contentScale = ContentScale.Crop
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { onSearchClicked() }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        disabledContainerColor = Color.Transparent,
                        cursorColor = Color.Black.copy(alpha = 0.38f),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        )
    }
}

@Preview
@Composable
private fun MainTopBarPreview() {
    MainTopBar(
        text = "",
        onSearchClicked = {},
        onTextChange = {},
        currentUser = User()
    )
}
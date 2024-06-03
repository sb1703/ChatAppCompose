package com.example.chatapp.presentation.screen.profile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.chatapp.R

@Composable
fun ProfileContent(
    name: String,
    mail: String,
    onNameChange: (String) -> Unit,
    profilePicture: String,
    isReadOnly: Boolean,
    onEditClicked: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .data(data = profilePicture)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .build(),
            contentDescription = "image",
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.padding(35.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(60.dp),
            shape = RoundedCornerShape(10.dp),
            value = name,
            onValueChange = { onNameChange(it) },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(0.38f)
                        .fillMaxWidth(),
                    text = "John Doe",
                    color = Color.Black
                )
            },
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = {  }
                ) {
                    Icon(
                        imageVector = Icons.Filled.People,
                        contentDescription = "People Icon",
                        tint = if(isSystemInDarkTheme()) Color.White.copy(alpha = 0.38f) else Color.Black.copy(alpha = 0.38f)
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = { onEditClicked() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Icon"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {  }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = Color.Transparent,
                cursorColor = Color.Black.copy(alpha = 0.38f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            readOnly = isReadOnly
        )
        Spacer(modifier = Modifier.padding(10.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(60.dp),
            shape = RoundedCornerShape(10.dp),
            value = mail,
            onValueChange = {  },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(0.38f)
                        .fillMaxWidth(),
                    text = "johndoe@example.com",
                    color = Color.Black
                )
            },
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = {  }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mail,
                        contentDescription = "Mail Icon",
                        tint = if(isSystemInDarkTheme()) Color.White.copy(alpha = 0.38f) else Color.Black.copy(alpha = 0.38f)
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {  }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Icon"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {  }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
//                    disabledContainerColor = Color.Transparent,
                cursorColor = Color.Black.copy(alpha = 0.38f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            readOnly = true
        )
    }

}

@Preview
@Composable
private fun ProfileContentPreview() {
    ProfileContent(
        name = "John Doe",
        mail = "johndoe@gmail.com",
        onNameChange = {},
        isReadOnly = true,
        onEditClicked = {},
        profilePicture = ""
    )
}
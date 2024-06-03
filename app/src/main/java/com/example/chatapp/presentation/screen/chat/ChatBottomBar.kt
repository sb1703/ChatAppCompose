package com.example.chatapp.presentation.screen.chat

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ChatBottomBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClicked: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {  }
        ) {
            Icon(
                modifier = Modifier.weight(1f),
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Icon",
                tint = if(isSystemInDarkTheme()) Color.White.copy(alpha = 0.38f) else Color.Black.copy(alpha = 0.38f)
            )
        }
        OutlinedTextField(
            modifier = Modifier.weight(8f).padding(5.dp),
            value = text,
            onValueChange = { onTextChange(it) },
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = { onSendClicked() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                disabledContainerColor = Color.Transparent,
                cursorColor = Color.Black.copy(alpha = 0.38f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            maxLines = 5
        )
        IconButton(
            onClick = { onSendClicked() }
        ) {
            Icon(
                modifier = Modifier.weight(1f),
                imageVector = Icons.Filled.Send,
                contentDescription = "Send Icon",
                tint = if(isSystemInDarkTheme()) Color.White.copy(alpha = 0.38f) else Color.Black.copy(alpha = 0.38f)
            )
        }
    }
}

@Preview
@Composable
private fun ChatBottomBarPreview() {
    ChatBottomBar(
//        text = "Hello World!",
        text = "HelloWorldfskljfsfjksjflfjslkfjkdjflksjflkdjflskfjlkfjlskjflksdjflksdfjsldkfjklskdjflksdjfjslkfjlksdlfdsjjlskfjsdlfkjsdfslfjlkHelloWorldfskljfsfjksjflfjslkfjkdjflksjflkdjflskfjlkfjlskjflksdjflksdfjsldkfjklskdjflksdjfjslkfjlksdlfdsjjlskfjsdlfkjsdfslfjlkHelloWorldfskljfsfjksjflfjslkfjkdjflksjflkdjflskfjlkfjlskjflksdjflksdfjsldkfjklskdjflksdjfjslkfjlksdlfdsjjlskfjsdlfkjsdfslfjlk",
        onTextChange = {},
        onSendClicked = {}
    )
}
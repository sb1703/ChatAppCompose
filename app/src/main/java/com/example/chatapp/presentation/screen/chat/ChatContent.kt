package com.example.chatapp.presentation.screen.chat

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.chatapp.R
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.SeenBy
import com.example.chatapp.domain.model.User

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatContent(
    chats: List<Message>,
    currentUser: User,
    currentUserId: String,
    chatUser: User
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        reverseLayout = true
    ){
        items(
            count = chats.size
        ){ index ->
            chats[index]?.let {
                if(it.author == currentUserId){
                    it.messageText?.let { it1 ->
                        ChatItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            text = it1,
                            onSendClicked = { /*TODO*/ },
                            dateTime = it.time,
                            seen = checkIfSeen(
                                seenBy = it.seenBy,
                                currentUserId = currentUserId,
                                chatUserId = chatUser.userId.toString(),
                                authorUserId = it.author
                            )
                        )
                    } ?: Log.d("debugging","messageText null")
                } else {
                    it.messageText?.let { it1 ->
                        OppChatItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            text = it1,
                            onSendClicked = { /*TODO*/ },
                            author = chatUser.name,
                            profilePhoto = chatUser.profilePhoto,
                            dateTime = it.time,
                            seen = checkIfSeen(
                                seenBy = it.seenBy,
                                currentUserId = currentUserId,
                                chatUserId = chatUser.userId.toString(),
                                authorUserId = it.author
                            )
                        )
                    } ?: Log.d("debugging","messageText null")
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    modifier: Modifier,
    text: String,
    onSendClicked: () -> Unit,
    dateTime: String,
    seen: Boolean
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f),
//                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Chat(
                    text = text,
                    onSendClicked
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 5.dp),
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(dateTime)
                        }
                    },
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AnimatedVisibility(visible = seen) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seen",
                        tint = Color.Blue
                    )
                }
            }
        }
    }
}

@Composable
fun OppChatItem(
    modifier: Modifier,
    text: String,
    onSendClicked: () -> Unit,
    author: String,
    profilePhoto: String,
    dateTime: String,
    seen: Boolean
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f),
//                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                modifier = Modifier.padding(start = 80.dp),
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                        append(author)
                    }
                },
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(50.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(data = profilePhoto)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .build(),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop
                )
                Chat(
                    text = text,
                    onSendClicked
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 5.dp),
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(dateTime)
                        }
                    },
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AnimatedVisibility(visible = seen) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Seen",
                        tint = Color.Blue
                    )
                }
            }
        }
    }
}


@Composable
fun Chat(
    text: String,
    onSendClicked: () -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        value = text,
        onValueChange = {  },
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
        readOnly = true
    )
}

@Preview(showBackground = true)
@Composable
private fun ChatItemPreview() {
    ChatItem(
        text = "textssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssh",
        onSendClicked = {},
        dateTime = "3:25 PM",
        seen = true,
        modifier = Modifier.fillMaxWidth()
    )
}


@Preview(showBackground = true)
@Composable
private fun OppChatItemPreview() {
    OppChatItem(
        text = "textssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssh",
        onSendClicked = {},
        author = "Shreyas 123",
        profilePhoto = "",
        dateTime = "3:25 PM",
        seen = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun ChatPreview() {
    Chat(
        text = "textssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssh",
        onSendClicked = {}
    )
}

private fun checkIfSeen(
    seenBy: List<SeenBy>?,
    currentUserId: String,
    chatUserId: String,
    authorUserId: String?
): Boolean {
    return if(authorUserId == currentUserId) {
        chatUserId in (seenBy?.map { it.userId } ?: emptyList())
    } else {
        currentUserId in (seenBy?.map { it.userId } ?: emptyList())
    }
}
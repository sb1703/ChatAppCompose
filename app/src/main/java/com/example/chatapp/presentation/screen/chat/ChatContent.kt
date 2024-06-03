package com.example.chatapp.presentation.screen.chat

import android.util.Log
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.chatapp.R
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User

@Composable
fun ChatContent(
//    chats: LazyPagingItems<Message>,
    chats: List<Message>,
    currentUser: User,
    currentUserId: String,
    chatUser: User
) {
//    val result = handlePagingResult(chats = chats)
//    Log.d("chatContentDebug","MainContent")
//    Log.d("chatContentDebug",chats.loadState.toString())
//    Log.d("chatContentDebug",result.toString())

//    if(result) {
//        LazyColumn(
//            modifier = Modifier.fillMaxSize()
//        ){
//            items(
//                count = chats.itemCount,
//                key = chats.itemKey { it.messageId!! }
//            ){ index ->
//                chats[index]?.let {
//                    Log.d("chatContentDebug",it.author.toString())
//                    Log.d("chatContentDebug",currentUser.name)
//                    if(it.author == currentUser.userId){
//                        it.messageText?.let { it1 ->
//                            ChatItem(
//                                text = it1,
//                                onSendClicked = { /*TODO*/ },
//                                dateTime = it.time
//                            )
//                        } ?: Log.d("chatContentDebug","messageText null")
//                    } else {
//                        it.messageText?.let { it1 ->
//                            OppChatItem(
//                                text = it1,
//                                onSendClicked = { /*TODO*/ },
//                                author = chatUser.name,
//                                profilePhoto = chatUser.profilePhoto,
//                                dateTime = it.time
//                            )
//                        } ?: Log.d("chatContentDebug","messageText null")
//                    }
//                }
//            }
//        }
//    }
//    Log.d("debugging",chats.isEmpty().toString())
    Log.d("debugging",chats.size.toString())
    Log.d("debugging",chats.toString())
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        reverseLayout = true
    ){
        items(
            count = chats.size
        ){ index ->
            chats[index]?.let {
                Log.d("debugging","author: ${it.author.toString()}")
//                Log.d("chatContentDebug","currentUser_name: ${currentUser.name}")
                Log.d("debugging","currentUser_userId: $currentUserId")
                if(it.author == currentUserId){
                    it.messageText?.let { it1 ->
                        Log.d("debugging","ChatItem")
                        ChatItem(
                            text = it1,
                            onSendClicked = { /*TODO*/ },
                            dateTime = it.time
                        )
                    } ?: Log.d("debugging","messageText null")
                } else {
                    it.messageText?.let { it1 ->
                        Log.d("debugging","OppChatItem")
                        OppChatItem(
                            text = it1,
                            onSendClicked = { /*TODO*/ },
                            author = chatUser.name,
                            profilePhoto = chatUser.profilePhoto,
                            dateTime = it.time
                        )
                    } ?: Log.d("debugging","messageText null")
                }
            }
        }
    }
}

@Composable
fun handlePagingResult(
    chats: LazyPagingItems<Message>
): Boolean {
    chats.apply {
        Log.d("chatContentDebug","HandlePagingResult")
        val error = when{
            loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
            loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
            loadState.append is LoadState.Error -> loadState.append as LoadState.Error
            else -> null
        }

        Log.d("chatContentDebug",error.toString())

        return when{
            loadState.refresh is LoadState.Loading -> {
                Log.d("chatContentDebug","ShimmerEffect")
//                ShimmerEffect()
                false
            }
            error != null -> {
                Log.d("chatContentDebug","error!=null")
//                EmptyScreen(error = error,heroes = heroes)
                false
            }
            chats.itemCount < 1 -> {
                Log.d("chatContentDebug","itemCount < 1")
//                EmptyScreen()
                false
            }
            else -> true
        }
    }
}

@Composable
fun ChatItem(
    text: String,
    onSendClicked: () -> Unit,
    dateTime: String
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
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
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(dateTime)
                        }
                    },
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun OppChatItem(
    text: String,
    onSendClicked: () -> Unit,
    author: String,
    profilePhoto: String,
    dateTime: String
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
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
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                            append(dateTime)
                        }
                    },
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
        dateTime = "3:25 PM"
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
        dateTime = "3:25 PM"
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
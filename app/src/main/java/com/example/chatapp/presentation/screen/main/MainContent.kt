package com.example.chatapp.presentation.screen.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toLocalTime

@Composable
fun MainContent(
    users: LazyPagingItems<User>,
    searchedUsers: LazyPagingItems<User>,
    searchQuery: String,
    navigationToChatScreen: (String) -> Unit,
    fetchLastMessage: (String) -> Message?,
    getAuthorName: (String) -> String
) {

    val result = handlePagingResult(users = users)
    val searchedResult = handlePagingResult(users = searchedUsers)
    Log.d("ListContent","MainContent")
    Log.d("ListContent",users.loadState.toString())
    Log.d("ListContent",searchedUsers.loadState.toString())
    Log.d("ListContent",result.toString())
    Log.d("ListContent",searchedResult.toString())
    if(searchQuery == "") {
        if(result) {
            UserList(
                users = users,
                navigationToChatScreen = navigationToChatScreen,
                fetchLastMessage = fetchLastMessage,
                getAuthorName = getAuthorName
            )
//            LazyColumn(
//                modifier = Modifier.fillMaxSize()
//            ){
//                items(
//                    count = users.itemCount,
//                    key = users.itemKey { it.userId!! }
//                ){ index ->
//                    users[index]?.let {
//                        it.userId?.let { it1 ->
//                            val message = fetchLastMessage(it1)
//                            val authorName = message?.author?.let { it2 -> getAuthorName(it2) }
//                            Log.d("lastMessageDebug",message.toString())
//                            if(message?.messageText != null) {
//                                Log.d("lastMessageDebug","message-not-null")
//                                Log.d("lastMessageDebug","message-text-not-null")
//                                UserItem(
//                                    title = it.name,
//                                    description = "${authorName}: ${message.messageText}",
//                                    time = message.time,
//                                    isUnread = true,
//                                    imageUri = it.profilePhoto,
//                                    navigationToChatScreen = { string ->
//                                        it.userId.let { it1 ->
//                                            navigationToChatScreen(
//                                                it1
//                                            )
//                                        }
//                                    }
//                                )
//                            } else {
//                                Log.d("lastMessageDebug","message-null or message-text-null")
//                                UserItem(
//                                    title = it.name,
//                                    description = "",
//                                    time = "",
//                                    isUnread = true,
//                                    imageUri = it.profilePhoto,
//                                    navigationToChatScreen = { string ->
//                                        it.userId.let { it1 ->
//                                            navigationToChatScreen(
//                                                it1
//                                            )
//                                        }
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
        }
    } else {
        if(searchedResult) {
            UserList(
                users = searchedUsers,
                navigationToChatScreen = navigationToChatScreen,
                fetchLastMessage = fetchLastMessage,
                getAuthorName = getAuthorName
            )
//            LazyColumn(
//                modifier = Modifier.fillMaxSize()
//            ){
//                items(
//                    count = searchedUsers.itemCount,
//                    key = searchedUsers.itemKey { it.userId!! }
//                ){ index ->
//                    searchedUsers[index]?.let {
//                        it.userId?.let { it1 ->
//                            val message = fetchLastMessage(it1)
//                            val authorName = message?.author?.let { it2 -> getAuthorName(it2) }
//                            Log.d("lastMessageDebug",message.toString())
//                            if(message?.messageText != null) {
//                                Log.d("lastMessageDebug","message-not-null")
//                                Log.d("lastMessageDebug","message-text-not-null")
//                                UserItem(
//                                    title = it.name,
//                                    description = "${authorName}: ${message.messageText}",
//                                    time = message.time,
//                                    isUnread = true,
//                                    imageUri = it.profilePhoto,
//                                    navigationToChatScreen = { string ->
//                                        it.userId.let { it1 ->
//                                            navigationToChatScreen(
//                                                it1
//                                            )
//                                        }
//                                    }
//                                )
//                            } else {
//                                Log.d("lastMessageDebug","message-null or message-text-null")
//                                UserItem(
//                                    title = it.name,
//                                    description = "",
//                                    time = "",
//                                    isUnread = true,
//                                    imageUri = it.profilePhoto,
//                                    navigationToChatScreen = { string ->
//                                        it.userId.let { it1 ->
//                                            navigationToChatScreen(
//                                                it1
//                                            )
//                                        }
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
        }
    }
}

@Composable
fun UserList(
    users: LazyPagingItems<User>,
    navigationToChatScreen: (String) -> Unit,
    fetchLastMessage: (String) -> Message?,
    getAuthorName: (String) -> String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        items(
            count = users.itemCount,
            key = users.itemKey { it.userId!! }
        ){ index ->
            users[index]?.let {
                it.userId?.let { it1 ->
                    val message = fetchLastMessage(it1)
                    val authorName = message?.author?.let { it2 -> getAuthorName(it2) }
                    Log.d("lastMessageDebug",message.toString())
                    if(message?.messageText != null) {
                        Log.d("lastMessageDebug","message-not-null")
                        Log.d("lastMessageDebug","message-text-not-null")
                        UserItem(
                            title = it.name,
                            description = "${authorName}: ${message.messageText}",
                            time = message.time,
                            isUnread = true,
                            imageUri = it.profilePhoto,
                            navigationToChatScreen = { string ->
                                it.userId.let { it1 ->
                                    navigationToChatScreen(
                                        it1
                                    )
                                }
                            }
                        )
                    } else {
                        Log.d("lastMessageDebug","message-null or message-text-null")
                        UserItem(
                            title = it.name,
                            description = "",
                            time = "",
                            isUnread = true,
                            imageUri = it.profilePhoto,
                            navigationToChatScreen = { string ->
                                it.userId.let { it1 ->
                                    navigationToChatScreen(
                                        it1
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun handlePagingResult(
    users: LazyPagingItems<User>
): Boolean {
    users.apply {
        Log.d("ListContent","HandlePagingResult")
        val error = when{
            loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
            loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
            loadState.append is LoadState.Error -> loadState.append as LoadState.Error
            else -> null
        }

        Log.d("ListContent",error.toString())

        return when{
            loadState.refresh is LoadState.Loading -> {
                Log.d("ListContent","ShimmerEffect")
//                ShimmerEffect()
                false
            }
            error != null -> {
                Log.d("ListContent","error!=null")
//                EmptyScreen(error = error,heroes = heroes)
                false
            }
            users.itemCount < 1 -> {
                Log.d("ListContent","itemCount < 1")
//                EmptyScreen()
                false
            }
            else -> true
        }
    }
}

@Composable
fun UserItem(
    title: String,
    description: String,
    time: String,
    isUnread: Boolean,
    imageUri: String,
    navigationToChatScreen: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable {
                navigationToChatScreen("")
            },
//            .background(Color.White),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
//                .height(70.dp)
                .fillMaxHeight(0.95f),
//                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(.7f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ){
                if(isUnread) {
                    Surface(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape),
                        color = Color.Blue
                    ) {}
                }
            }
            Box(
                modifier = Modifier.weight(1.5f),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(data = imageUri)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .build(),
                    contentDescription = "image",
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(8f),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.95f),
                    text = title,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.8f),
                    text = description,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = time,
                    color = Color.Gray
                )
            }
        }
        Surface(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth(0.82f),
            color = Color.LightGray
        ) {  }
    }
}

@Preview
@Composable
private fun UserItemPreview() {
    UserItem(
        title = "Title",
        description = "Descriptionsifjskdlfjfjkjfdkjfksjdkjkfjskfjkjfkjfjsdjfjskfjslkfjfkdjkfjskfjfskdjfldkfksldfdfjskjdf",
        time = "3:25 PM",
        isUnread = true,
        imageUri = "",
        navigationToChatScreen = {}
    )
}
package com.example.chatapp.presentation.screen.main

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.chatapp.domain.model.UserItem
import com.example.chatapp.domain.model.UserListData

@Composable
fun MainContent(
    users: List<UserItem>,
    searchedUsers: LazyPagingItems<UserItem>,
    searchQuery: String,
    navigationToChatScreen: (String) -> Unit,
    getAuthorName: (String) -> String
) {

    val searchedResult = handlePagingResult(users = searchedUsers)
    if(searchQuery == "") {
        UserList(
            userListData = UserListData.RegularData(users = users),
            navigationToChatScreen = navigationToChatScreen,
            getAuthorName = getAuthorName
        )
    } else {
        if(searchedResult) {
            UserList(
                userListData = UserListData.PagingData(users = searchedUsers),
                navigationToChatScreen = navigationToChatScreen,
                getAuthorName = getAuthorName
            )
        }
    }
}

@Composable
fun UserList(
    userListData: UserListData,
    navigationToChatScreen: (String) -> Unit,
    getAuthorName: (String) -> String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        when(userListData) {
            is UserListData.PagingData -> {
                items(
                    count = userListData.users.itemCount,
                    key = userListData.users.itemKey { it.userId!! }
                ){ index ->
                    userListData.users[index]?.let {
                        it.userId?.let { it1 ->
                            val message = it.lastMessage
                            val authorName = message?.author?.let { it2 -> getAuthorName(it2) }
                            if(message?.messageText != null) {
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
            is UserListData.RegularData -> {
                items(
                    count = userListData.users.size,
                    key = { userListData.users[it].userId!! }
                ) { index ->
                    val user = userListData.users[index]

                    if(user.isTyping) {
                        Log.d("lazyColumnTyping", "isTyping: ${user.name}")
                    }

                    val message = user.lastMessage
                    val authorName = message?.author?.let { getAuthorName(it) }
                    Log.d("lazyColumn", "userId: ${user.userId}")

                    UserItem(
                        title = user.name,
                        description = "${authorName}: ${message?.messageText.orEmpty()}",
                        time = message?.time.orEmpty(),
                        isUnread = true,
                        imageUri = user.profilePhoto,
                        navigationToChatScreen = { user.userId?.let { it1 ->
                            navigationToChatScreen(
                                it1
                            )
                        } }
                    )
//                    userListData.users[index].let {
//                        it.userId?.let { it1 ->
//                            Log.d("lazyColumn","userId: $it1")
//                            val message = it.lastMessage
//                            val authorName = message?.author?.let { it2 -> getAuthorName(it2) }
//                            if(message?.messageText != null) {
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
                }
            }
        }
    }
}

@Composable
fun handlePagingResult(
    users: LazyPagingItems<UserItem>
): Boolean {
    users.apply {
        val error = when{
            loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
            loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
            loadState.append is LoadState.Error -> loadState.append as LoadState.Error
            else -> null
        }

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
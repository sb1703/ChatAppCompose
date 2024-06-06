package com.example.chatapp.domain.model

import androidx.paging.compose.LazyPagingItems

sealed class UserListData {
    data class PagingData(val users: LazyPagingItems<UserItem>) : UserListData()
    data class RegularData(val users: List<UserItem>) : UserListData()
}

package com.example.chatapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chatapp.data.paging_source.FetchChatsSource
import com.example.chatapp.data.paging_source.FetchUsersSource
import com.example.chatapp.data.paging_source.SearchUsersSource
import com.example.chatapp.data.remote.KtorApi
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserItem
import com.example.chatapp.domain.repository.RemoteDataSource
import com.example.chatapp.util.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow

class RemoteDataSourceImpl(
    private val ktorApi: KtorApi
): RemoteDataSource {

//    override fun fetchUsers(): Flow<PagingData<User>> {
//        return Pager(
//            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
//            pagingSourceFactory = {
//                FetchUsersSource(ktorApi = ktorApi)
//            }
//        ).flow
//    }

    override fun fetchChats(request: ApiRequest): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            pagingSourceFactory = {
                FetchChatsSource(ktorApi = ktorApi, request = request)
            }
        ).flow
    }

    override fun searchUsers(request: ApiRequest): Flow<PagingData<UserItem>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            pagingSourceFactory = {
                SearchUsersSource(ktorApi = ktorApi, request = request)
            }
        ).flow
    }


}
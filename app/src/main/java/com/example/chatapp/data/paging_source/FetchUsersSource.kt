package com.example.chatapp.data.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.chatapp.data.remote.KtorApi
import com.example.chatapp.domain.model.User
import javax.inject.Inject

class FetchUsersSource @Inject constructor(
    private val ktorApi: KtorApi
): PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try{
            val apiResponse = ktorApi.fetchUsers()
            val users = apiResponse.listUsers
            if(users.isNotEmpty()){
                return LoadResult.Page(
                    data = users,
                    prevKey = apiResponse.prevPage,
                    nextKey = apiResponse.nextPage
                )
            } else{
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition
    }

}
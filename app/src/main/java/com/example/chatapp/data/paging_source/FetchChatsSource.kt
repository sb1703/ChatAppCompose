package com.example.chatapp.data.paging_source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.chatapp.data.remote.KtorApi
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.Message
import javax.inject.Inject

class FetchChatsSource @Inject constructor(
    private val ktorApi: KtorApi,
    private val request: ApiRequest
): PagingSource<Int, Message>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        return try{
            Log.d("chatContentDebug","source")
            val apiResponse = ktorApi.fetchChats(
                request = request
            )
            val messages = apiResponse.listMessages
            if(messages.isNotEmpty()){
                Log.d("chatContentDebug","messages.isNotEmpty()")
                return LoadResult.Page(
                    data = messages,
                    prevKey = apiResponse.prevPage,
                    nextKey = apiResponse.nextPage
                )
            } else{
                Log.d("chatContentDebug","messages.isEmpty()")
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        } catch (e: Exception){
            Log.d("chatContentDebug","source-error")
            Log.d("chatContentDebug",e.message.toString())
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition
    }

}
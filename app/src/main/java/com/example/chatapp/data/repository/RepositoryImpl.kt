package com.example.chatapp.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chatapp.data.paging_source.FetchChatsSource
import com.example.chatapp.data.paging_source.FetchUsersSource
import com.example.chatapp.data.paging_source.SearchUsersSource
import com.example.chatapp.data.remote.KtorApi
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.ApiResponse
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserItem
import com.example.chatapp.domain.model.UserUpdate
import com.example.chatapp.domain.repository.DataStoreOperations
import com.example.chatapp.domain.repository.RemoteDataSource
import com.example.chatapp.domain.repository.Repository
import com.example.chatapp.util.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val dataStoreOperations: DataStoreOperations,
    private val remote: RemoteDataSource,
    private val ktorApi: KtorApi
): Repository {

    override suspend fun saveSignedInState(signedIn: Boolean) {
        dataStoreOperations.saveSignedInState(signedIn = signedIn)
    }

    override fun readSignedInState(): Flow<Boolean> {
        return dataStoreOperations.readSignedInState()
    }

    override suspend fun verifyTokenOnBackend(request: ApiRequest): ApiResponse {
        return try {
            Log.d("debug","repo")
            ktorApi.verifyTokenOnBackend(request = request)
        } catch (e: Exception) {
            Log.d("debug","repo-error")
            Log.d("debug",e.message.toString())
            ApiResponse(success = false, error = e)
        }
    }

    override suspend fun getUserInfo(): ApiResponse {
        return try {
            Log.d("helloWorld","repo")
            ktorApi.getUserInfo()
        } catch (e: Exception) {
            Log.d("helloWorld","repo-Exception")
            Log.d("helloWorld",e.message.toString())
            ApiResponse(success = false, error = e)
        }
    }

    override suspend fun getUserInfoById(request: ApiRequest): ApiResponse {
        return try {
            Log.d("hello","repo")
            ktorApi.getUserInfoById(
                request = request
            )
        } catch (e: Exception) {
            Log.d("hello",e.message.toString())
            ApiResponse(success = false, error = e)
        }
    }

    override suspend fun getOnlineStatus(request: ApiRequest): ApiResponse {
        return try {
            Log.d("hello","repo")
            ktorApi.getOnlineStatus(
                request = request
            )
        } catch (e: Exception) {
            Log.d("hello",e.message.toString())
            ApiResponse(success = false, error = e)
        }
    }

    override suspend fun getLastLogin(request: ApiRequest): ApiResponse {
        return try {
            Log.d("hello","repo")
            ktorApi.getLastLogin(
                request = request
            )
        } catch (e: Exception) {
            Log.d("hello",e.message.toString())
            ApiResponse(success = false, error = e)
        }
    }

    override suspend fun updateUser(userUpdate: UserUpdate): ApiResponse {
        return try {
            ktorApi.updateUser(userUpdate = userUpdate)
        } catch (e: Exception) {
            ApiResponse(success = false, error = e)
        }
    }

    override suspend fun deleteUser(): ApiResponse {
        return try {
            ktorApi.deleteUser()
        } catch (e: Exception) {
            ApiResponse(success = false, error = e)
        }
    }

    override suspend fun clearSession(): ApiResponse {
        return try {
            ktorApi.clearSession()
        } catch (e: Exception) {
            ApiResponse(success = false, error = e)
        }
    }

    override suspend fun addChats(request: ApiRequest): ApiResponse {
        return try {
            ktorApi.addChats(request = request)
        } catch (e: Exception) {
            ApiResponse(success = false, error = e)
        }
    }

    override suspend fun addUsers(request: ApiRequest): ApiResponse {
        return try {
            ktorApi.addUsers(request = request)
        } catch (e: Exception) {
            ApiResponse(success = false, error = e)
        }
    }

//    override suspend fun fetchChats(request: ApiRequest): Flow<PagingData<Message>> {
//        Log.d("chatContentDebug","repo")
//        return remote.fetchChats(request = request)
//    }

    override suspend fun fetchChats(request: ApiRequest): ApiResponse {
        Log.d("chatContentDebug","repo")
        return try {
            ktorApi.fetchChats(request = request)
        } catch (e: Exception) {
            ApiResponse(success = false, error = e)
        }
    }

    override suspend fun fetchLastChat(request: ApiRequest): ApiResponse {
        return try {
            Log.d("lastMessageDebug","repo")
            ktorApi.fetchLastChat(request = request)
        } catch (e: Exception) {
            Log.d("lastMessageDebug","repo-error")
            Log.d("lastMessageDebug",e.message.toString())
            ApiResponse(success = false, error = e)
        }
    }

//    override suspend fun fetchUsers(page: Int, limit: Int): Flow<PagingData<User>> {
//        return Pager(
//            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
//            pagingSourceFactory = {
//                FetchUsersSource(ktorApi = ktorApi, page = page, limit = limit)
//            }
//        ).flow
////        return try {
////            ktorApi.fetchUsers(page = page, limit = limit)
////        } catch (e: Exception) {
////            ApiResponse(success = false, error = e)
////        }
//    }

    override suspend fun fetchUsers(): ApiResponse {
//        return remote.fetchUsers()
        return ktorApi.fetchUsers()
    }

    override suspend fun searchUsers(request: ApiRequest): Flow<PagingData<UserItem>> {
        return remote.searchUsers(request = request)
    }

}
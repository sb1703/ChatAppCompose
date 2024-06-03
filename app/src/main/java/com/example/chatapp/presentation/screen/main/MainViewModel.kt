package com.example.chatapp.presentation.screen.main

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserUpdate
import com.example.chatapp.domain.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

//    private val _searchQuery = mutableStateOf("")
//    val searchQuery = _searchQuery

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _fetchedUser = MutableStateFlow<PagingData<User>>(PagingData.empty())
    val fetchedUser = _fetchedUser

    private val _searchedUser = MutableStateFlow<PagingData<User>>(PagingData.empty())
    val searchedUser = _searchedUser

//    private val _currentUser = mutableStateOf(User())
//    val currentUser = _currentUser

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

//    private val _lastMessage = mutableStateOf(Message())
//    val lastMessage = _lastMessage

    private val _lastMessage = MutableStateFlow<Message?>(Message())
    val lastMessage = _lastMessage.asStateFlow()

    private val _chatUser = MutableStateFlow(User())
    val chatUser = _chatUser.asStateFlow()

    private val _chatId = MutableStateFlow("")
    val chatId = _chatId.asStateFlow()

//    init {
//        fetchUsers()
//        Log.d("helloWorld","init")
//        getCurrentUser()
//        Log.d("helloWorld","a-init")
//    }

    fun updateSearchQuery(query: String){
        _searchQuery.value = query
    }

    fun updateChatId(query: String) {
        _chatId.value = query
    }

    fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentUser.value = repository.getUserInfo().user!!
        }
//        _currentUser.value = repository.getUserInfo().user!!
    }

    suspend fun fetchUsers(){
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.fetchUsers().cachedIn(viewModelScope).collect{
//                _fetchedUser.value = it
//            }
//        }
        repository.fetchUsers().cachedIn(viewModelScope).collect{
            _fetchedUser.value = it
        }
    }

    fun searchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.searchUsers(
                request = ApiRequest(
                    name = searchQuery.value,
                )
            ).cachedIn(viewModelScope).collect{
                _searchedUser.value = it
            }
        }
    }

    fun fetchLastChat(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _lastMessage.value = repository.fetchLastChat(request = ApiRequest(
                userId = userId
            )).chat
        }
    }

    fun getUserInfoByUserId() {
        viewModelScope.launch(Dispatchers.IO) {
            _chatUser.value = repository.getUserInfoById(request = ApiRequest(userId = chatId.value)).user!!
        }
    }

    suspend fun setOnlineTrue() {
        Log.d("debugging","SetOnlineTrue")
//        viewModelScope.launch(Dispatchers.IO) {
//            currentUser.value?.let {
//                UserUpdate(
//                    name = it.name,
//                    online = true
//                )
//            }?.let {
//                repository.updateUser(
//                    userUpdate = it
//                )
//            }
//        }
        currentUser.value?.let {
            UserUpdate(
                name = it.name,
                online = true
            )
        }?.let {
            repository.updateUser(
                userUpdate = it
            )
        }
        Log.d("debugging","SetOnlineTrueDone!")
    }

    suspend fun setOnlineFalse() {
        Log.d("debugging","SetOnlineFalse")
//        viewModelScope.launch(Dispatchers.IO) {
//            currentUser.value?.let {
//                UserUpdate(
//                    name = it.name,
//                    online = false
//                )
//            }?.let {
//                repository.updateUser(
//                    userUpdate = it
//                )
//            }
//        }
        currentUser.value?.let {
            UserUpdate(
                name = it.name,
                online = false
            )
        }?.let {
            repository.updateUser(
                userUpdate = it
            )
        }
        Log.d("debugging","SetOnlineFalseDone!")
    }

}
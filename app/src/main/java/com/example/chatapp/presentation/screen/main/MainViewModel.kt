package com.example.chatapp.presentation.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.chatapp.data.remote.ChatSocketService
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.ChatEvent
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.Typing
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserItem
import com.example.chatapp.domain.model.UserUpdate
import com.example.chatapp.domain.repository.Repository
import com.example.chatapp.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val chatSocketService: ChatSocketService
): ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _fetchedUser = MutableStateFlow<List<UserItem>>(emptyList())
    val fetchedUser = _fetchedUser.asStateFlow()

    private val _searchedUser = MutableStateFlow<PagingData<UserItem>>(PagingData.empty())
    val searchedUser = _searchedUser.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    fun connectToChat() {
        viewModelScope.launch {
            Log.d("debugging2","userId: ${currentUser.value.userId} connected")
            val result = currentUser.value.userId?.let { chatSocketService.initSession(it) }
            chatSocketService.sendOnline(true)
            when(result) {
                is RequestState.Success -> {
                    Log.d("debugging2","result is success")
                    chatSocketService.observeChatEvent()
                        .collectLatest { chatEvent ->
                            when(chatEvent) {
                                is ChatEvent.MessageEvent -> {
                                    handleMessageEvent(chatEvent)
                                }
                                is ChatEvent.TypingEvent -> {
                                    handleTypingEvent(chatEvent)
                                }
                                is ChatEvent.ListEvent -> {
                                    handleListEvent(chatEvent)
                                }
                                is ChatEvent.OnlineEvent -> {  }
                            }
                        }
                }
                is RequestState.Error -> {
                    Log.d("debugging","result is error")
                }
                else -> {
                    Log.d("debugging","result is else")
                }
            }
        }
    }

    private suspend fun handleMessageEvent(chatEvent: ChatEvent.MessageEvent) {
        Log.d("message", "message: ${chatEvent.receiverUserIds[0]} - ${chatEvent.messageText}")
        val user = fetchedUser.value.find { it.userId == chatEvent.receiverUserIds[0] }
        val newList = fetchedUser.value.filter { it.userId != chatEvent.receiverUserIds[0] }
        val newUser = user?.copy(
            lastMessage = Message(
                author = chatEvent.receiverUserIds[0],
                messageText = chatEvent.messageText,
                receiver = listOf(currentUser.value.userId)
            )
        )
        if(newUser != null){
            Log.d("message", "newMessageList: ${chatEvent.receiverUserIds[0]}")
            _fetchedUser.value = listOf(newUser) + newList
        }

//        val newList = fetchedUser.value.toMutableList().apply {
//            val index = indexOfFirst { it.userId == chatEvent.receiverUserIds[0] }
//            if(index != -1){
//                Log.d("message", "found index: $index")
//                val user = get(index)
//                val newUser = user.copy(
//                    lastMessage = Message(
//                        author = chatEvent.receiverUserIds[0],
//                        messageText = chatEvent.messageText,
//                        receiver = listOf(currentUser.value.userId)
//                    )
//                )
//                set(index,newUser)
//            } else {
//                Log.d("message", "user not found in the list")
//            }
//        }
//        _fetchedUser.value = newList
    }

    private suspend fun handleTypingEvent(chatEvent: ChatEvent.TypingEvent) {
        Log.d("typing", "typing: ${chatEvent.receiverUserIds[0]} typing...")
        fetchedUser.value.forEachIndexed { index, userItem ->
            if(userItem.userId == chatEvent.receiverUserIds[0]){
                userItem.typingJob?.cancel()
                val newUser = userItem.copy(
                    isTyping = true,
                    lastMessage = Message(
                        author = chatEvent.receiverUserIds[0],
                        messageText = chatEvent.typingText,
                        receiver = listOf(currentUser.value.userId)
                    ),
                    typingJob = viewModelScope.launch {
                        delay(1000) // Delay for 1 seconds
                        val newUser2 = userItem.copy(
                            isTyping = false,
                            lastMessage = repository.fetchLastChat(request = ApiRequest(
                                userId = userItem.userId
                            )).chat
                        )
                        val newList = fetchedUser.value.toMutableList().apply {
                            set(index,newUser2)
                        }
                        _fetchedUser.value = newList
                    }
                )
                val newList = fetchedUser.value.toMutableList().apply {
                    set(index,newUser)
                }
                Log.d("typing", "newTypingList: ${chatEvent.receiverUserIds[0]}")
                _fetchedUser.value = newList
            }
        }
    }

    private suspend fun handleListEvent(chatEvent: ChatEvent.ListEvent) {
        Log.d("list", "list: ${chatEvent.receiverUserIds[0]}")
        val newUser = repository.getUserInfoById(request = ApiRequest(userId = chatEvent.receiverUserIds[0])).user!!
        val newUserItem = UserItem(
            id = newUser.id,
            userId = newUser.userId,
            name = newUser.name,
            emailAddress = newUser.emailAddress,
            profilePhoto = newUser.profilePhoto,
            list = newUser.list,
            online = newUser.online,
            lastLogin = newUser.lastLogin,
            socket = newUser.socket,
            isTyping = false,
            lastMessage = repository.fetchLastChat(request = ApiRequest(
                userId = newUser.userId
            )).chat
        )
        _fetchedUser.value = listOf(newUserItem) + _fetchedUser.value
//        val newList = fetchedUser.value.toMutableList().apply {
//            add(0,newUserItem)
//        }
//        _fetchedUser.value = newList
    }

    fun disconnect() {
        Log.d("debugging2","disconnecting mainViewModel")
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

    fun updateSearchQuery(query: String){
        _searchQuery.value = query
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = repository.getUserInfo().user!!
        }
    }

    fun fetchUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            _fetchedUser.value = repository.fetchUsers().listUsers.map {
                UserItem(
                    id = it.id,
                    userId = it.userId,
                    name = it.name,
                    emailAddress = it.emailAddress,
                    profilePhoto = it.profilePhoto,
                    list = it.list,
                    online = it.online,
                    lastLogin = it.lastLogin,
                    socket = it.socket,
                    isTyping = false,
                    lastMessage = repository.fetchLastChat(request = ApiRequest(
                        userId = it.userId
                    )).chat
                )
            }
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

    suspend fun setOnlineFalse() {
        chatSocketService.sendOnline(false)
    }

//    private fun handleTypingEvent(typing: Typing) {
//        // Cancel any existing typing job
//        typingJob?.cancel()
//
//        // Update the typing state
//        _isTyping.value = true
//        _lastMessage.value = Message(
//            author = typing.author,
//            messageText = typing.typingText,
//            receiver = listOf(chatId.value)
//        )
//
//        // Reset the typing state after a delay
//        typingJob = viewModelScope.launch {
//            delay(1000) // Delay for 1 seconds
//            _isTyping.value = false
//            _lastMessage.value = (if(typing.author == currentUser.value.userId) typing.receiver[0] else typing.author)?.let {
//                fetchLastChatAlt(
//                    it
//                )
//            }
//        }
//    }

}
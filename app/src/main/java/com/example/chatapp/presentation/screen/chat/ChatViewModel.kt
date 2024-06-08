package com.example.chatapp.presentation.screen.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.chatapp.data.remote.ChatSocketService
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.ChatEvent
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.repository.Repository
import com.example.chatapp.util.Constants.CHAT_USER_ID
import com.example.chatapp.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: Repository,
    private val chatSocketService: ChatSocketService
): ViewModel() {

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    private val _chatUser = MutableStateFlow(User())
    val chatUser = _chatUser.asStateFlow()

    private val _chatId = MutableStateFlow("")
    val chatId = _chatId.asStateFlow()

    private val _fetchedChat = MutableStateFlow<List<Message>>(emptyList())
    val fetchedChat = _fetchedChat.asStateFlow()

    private val _chatText = MutableStateFlow("")
    val chatText = _chatText.asStateFlow()

    private val _online = MutableStateFlow(false)
    val online = _online.asStateFlow()

    init {
        getCurrentUser()
        getChatIdArgument()
        viewModelScope.launch {
            _chatId.collectLatest { chatId ->
                if(chatId.isNotBlank()) {
                    fetchChats()
                    getUserInfoByUserId()
                    _currentUser.collectLatest { user ->
                        if(user.userId != null) {
                            connectToChat()
                            _chatUser.collectLatest { chatUser ->
                                if(chatUser.userId != null) {
                                    setOnline()
                                }
                            }
                        }
                    }
                }
            }
        }
//        _chatId.zip(_currentUser) { chatId, currentUser ->
//            chatId to currentUser
//        }.onEach { (chatId, currentUser) ->
//            Log.d("debugging3", "chatId: $chatId currentUser: $currentUser zip")
//            if(chatId.isNotBlank() && currentUser.userId != null) {
//                viewModelScope.launch {
//                    fetchChats()
//                    getUserInfoByUserId()
//                    connectToChat()
//                }
//            }
//        }.combine(_chatUser) { _, chatUser ->
//            chatUser
//        }.onEach { chatUser ->
//            if(chatUser.userId != null) {
//                setOnline()
//            }
//        }.launchIn(viewModelScope)
    }

    fun connectToChat() {
        viewModelScope.launch {
            Log.d("debugging2","userId: ${currentUser.value.userId} connected chat")
            val result = currentUser.value.userId?.let { chatSocketService.initSession(it) }
            when(result) {
                is RequestState.Success -> {
                    Log.d("debugging2","result is success")
                    chatSocketService.observeChatEvent()
                        .collectLatest { chatEvent ->
                            when(chatEvent) {
                                is ChatEvent.MessageEvent -> {
                                    handleMessageEvent(chatEvent, currentUser.value)
                                }
                                is ChatEvent.TypingEvent -> {  }
                                is ChatEvent.OnlineEvent -> {
                                    handleOnlineEvent(chatEvent)
                                }
                                is ChatEvent.ListEvent -> {  }
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
//        viewModelScope.launch {
//            Log.d("debugging2","userId: ${currentUser.value.userId} observe chat")
//            chatSocketService.observeChatEvent()
//                .collectLatest { chatEvent ->
//                    when(chatEvent) {
//                        is ChatEvent.MessageEvent -> {
//                            handleMessageEvent(chatEvent, currentUser.value)
//                        }
//                        is ChatEvent.TypingEvent -> {  }
//                        is ChatEvent.OnlineEvent -> {
//                            handleOnlineEvent(chatEvent)
//                        }
//                        is ChatEvent.ListEvent -> {  }
//                    }
//                }
//        }
    }

    private suspend fun handleMessageEvent(chatEvent: ChatEvent.MessageEvent, currentUser: User?) {
        withContext(Dispatchers.IO) {
            Log.d("message", "message: ${chatEvent.receiverUserIds[0]} - ${chatEvent.messageText} chat")
//            val newList = fetchedChat.value.toMutableList().apply {
//                add(0,Message(
//                    author = chatEvent.receiverUserIds[0],
//                    messageText = chatEvent.messageText,
//                    receiver = listOf(currentUser?.userId)
//                ))
//            }
            val newMessage = Message(
                author = chatEvent.receiverUserIds[0],
                messageText = chatEvent.messageText,
                receiver = listOf(currentUser?.userId)
            )
            _fetchedChat.value = listOf(newMessage) + _fetchedChat.value
        }
    }

    private suspend fun handleOnlineEvent(chatEvent: ChatEvent.OnlineEvent) {
        withContext(Dispatchers.IO) {
            _online.value = chatEvent.online
        }
    }

    fun disconnect() {
        Log.d("disconnect","disconnecting chat")
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    fun sendMessage(currentUser: User?) {
        viewModelScope.launch {
            val currentChatText = chatText.value
            if(currentChatText.isNotBlank()) {
                var isPresent = false
                currentUser?.list?.forEach{
                    if(it == chatId.value){
                        isPresent = true
                    }
                }
                if(currentUser != null && !isPresent){
                    repository.addUsers(
                        request = ApiRequest(
                            userId = chatId.value
                        )
                    )
                    chatSocketService.sendList(receiverUserIds = listOf(chatId.value))
                }
                val newList = fetchedChat.value.toMutableList().apply {
                    add(0, Message(
                        author = currentUser?.userId,
                        messageText = currentChatText,
                        receiver = listOf(chatId.value)
                    ))
                }
                _fetchedChat.value = newList
                chatSocketService.sendMessage(message = currentChatText, receiverUserIds = listOf(chatId.value))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("disconnect","onCleared chat")
        disconnect()
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = repository.getUserInfo().user!!
        }
    }

    fun updateChatText(query: String) {
        viewModelScope.launch {
            chatSocketService.sendTyping(receiverUserIds = listOf(chatId.value))
        }
        _chatText.value = query
    }

    fun clearChatText() {
        _chatText.value = ""
    }

    fun getChatIdArgument() {
        _chatId.value = savedStateHandle.get<String> (
            key = CHAT_USER_ID
        ).toString()
    }

    suspend fun fetchChats(){
        _fetchedChat.value = repository.fetchChats(
            request = ApiRequest(
                userId = chatId.value
            )
        ).listMessages.reversed()
    }

    suspend fun getUserInfoByUserId() {
        _chatUser.value = repository.getUserInfoById(request = ApiRequest(userId = chatId.value)).user!!
    }

    fun setOnline() {
        viewModelScope.launch {
            _online.value = chatUser.value.online
        }
    }

    fun setOnlineFalse() {
        viewModelScope.launch {
            chatSocketService.sendOnline(false)
        }
    }

}
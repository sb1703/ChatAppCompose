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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: Repository,
    private val chatSocketService: ChatSocketService
): ViewModel() {

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
        viewModelScope.launch {
            getChatIdArgument()
            getUserInfoByUserId()
            setOnline()
            fetchChats()
        }
    }

    fun connectToChat(
        currentUser: User?
    ) {
        viewModelScope.launch {
            chatSocketService.observeChatEvent()
                .collectLatest { chatEvent ->
                    when(chatEvent) {
                        is ChatEvent.MessageEvent -> {
                            handleMessageEvent(chatEvent, currentUser)
                        }
                        is ChatEvent.TypingEvent -> {  }
                        is ChatEvent.OnlineEvent -> {
                            handleOnlineEvent(chatEvent)
                        }
                        is ChatEvent.ListEvent -> {  }
                    }
                }
        }
    }

    private fun handleMessageEvent(chatEvent: ChatEvent.MessageEvent, currentUser: User?) {
        val newList = fetchedChat.value.toMutableList().apply {
            add(0,Message(
                author = chatEvent.receiverUserIds[0],
                messageText = chatEvent.messageText,
                receiver = listOf(currentUser?.userId)
            ))
        }
        _fetchedChat.value = newList
    }

    private fun handleOnlineEvent(chatEvent: ChatEvent.OnlineEvent) {
        _online.value = chatEvent.online
    }

    fun disconnect() {
        Log.d("debugging2","disconnecting chatViewModel")
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
        disconnect()
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

}
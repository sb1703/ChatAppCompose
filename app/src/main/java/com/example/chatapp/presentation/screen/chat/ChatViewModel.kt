package com.example.chatapp.presentation.screen.chat

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.chatapp.data.remote.ChatSocketService
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.repository.Repository
import com.example.chatapp.util.Constants.CHAT_USER_ID
import com.example.chatapp.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

//    private val _fetchedChat = MutableStateFlow<PagingData<Message>>(PagingData.empty())
//    val fetchedChat = _fetchedChat

    private val _fetchedChat = MutableStateFlow<List<Message>>(emptyList())
    val fetchedChat = _fetchedChat.asStateFlow()

    private val _chatText = MutableStateFlow("")
    val chatText = _chatText.asStateFlow()

//    private val _currentUser = MutableStateFlow<User?>(User())
//    val currentUser = _currentUser.asStateFlow()

//    init {
//        getChatIdArgument()
//        viewModelScope.launch(Dispatchers.Default) {
//            getCurrentUser()
//            getUserInfoByUserId()
//            fetchChats()
//        }
//        connectToChat()
//        Log.d("debugging","currentUser: ${currentUser.value?.userId}")
//        Log.d("debugging","chatId: ${chatId.value}")
//        Log.d("debugging","chatUser: ${chatUser.value.name}")
//        Log.d("debugging","fetchedChats: ${fetchedChat.value.size}")
//    }

    fun connectToChat(
        currentUser: User?
    ) {
        Log.d("debugging2","connected to chat")
        Log.d("debugging","currentUser: ${currentUser?.userId}")
        Log.d("debugging","chatId: ${chatId.value}")
        Log.d("debugging","chatUser: ${chatUser.value.name}")
        Log.d("debugging","fetchedChats: ${fetchedChat.value.size}")
//        viewModelScope.launch(Dispatchers.Main) {
//            getUserInfoByUserId()
//            fetchChats()
//        }
        viewModelScope.launch {
            Log.d("debugging","senderUserId: ${currentUser?.userId} && receiver: ${chatId.value}")
            val result = currentUser?.userId?.let { chatSocketService.initSession(it,listOf(chatId.value)) }
            when(result) {
                is RequestState.Success -> {
                    Log.d("debugging","result is success")
                    chatSocketService.observeMessage()
                        .onEach { message ->
                            val newList = fetchedChat.value.toMutableList().apply {
                                add(0,message)
                            }
                            _fetchedChat.value = newList
                        }.launchIn(viewModelScope)
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

//    suspend fun getCurrentUser() {
////        viewModelScope.launch(Dispatchers.IO) {
////            Log.d("debugging","getCurrentUser: ${repository.getUserInfo().user}")
////            _currentUser.value = repository.getUserInfo().user!!
////        }
//        Log.d("chatContentDebug","getCurrentUser: ${repository.getUserInfo().user}")
//        _currentUser.value = repository.getUserInfo().user!!
//        Log.d("chatContentDebug","getCurrentUserDone!: ${currentUser.value?.name}")
//    }

    fun disconnect() {
        Log.d("debugging","disconnect fn")
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

//    fun fetchMessages() {
//        viewModelScope.launch {
//
//        }
//    }

    fun sendMessage(currentUser: User?) {
        viewModelScope.launch {
            val currentChatText = chatText.value
            Log.d("debugging2","sendMessage1: ${currentChatText}")
            if(currentChatText.isNotBlank()) {
                var isPresent = false
                currentUser?.list?.forEach{
//                    if(it.userId == chatId.value){
//                        isPresent = true
//                    }
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
                }
                val newList = fetchedChat.value.toMutableList().apply {
                    add(0, Message(
                        author = currentUser?.userId,
                        messageText = currentChatText,
                        receiver = listOf(chatId.value)
                    ))
                }
                _fetchedChat.value = newList
                Log.d("debugging2","sendMessage2: ${currentChatText}")
                chatSocketService.sendMessage(currentChatText)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("debugging","disconnected")
        disconnect()
    }

    fun updateChatText(query: String) {
        Log.d("debugging","updating")
        _chatText.value = query
        Log.d("debugging","updated: ${chatText.value}")
    }

    fun clearChatText() {
        Log.d("debugging","clear Text")
        _chatText.value = ""
    }

    fun getChatIdArgument() {
        Log.d("debugging","getChatIdArgument")
        _chatId.value = savedStateHandle.get<String> (
            key = CHAT_USER_ID
        ).toString()
    }

//    fun fetchChats(){
//        Log.d("chatContentDebug",chatId.value)
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.fetchChats(
//                request = ApiRequest(
//                    userId = chatId.value
//                )
//            ).cachedIn(viewModelScope).collect{
//                _fetchedChat.value = it
//            }
//        }
//    }

    suspend fun fetchChats(){
        Log.d("chatContentDebug",chatId.value)
//        viewModelScope.launch(Dispatchers.IO) {
//            Log.d("debugging","fetchedChats")
//            _fetchedChat.value = repository.fetchChats(
//                request = ApiRequest(
//                    userId = chatId.value
//                )
//            ).listMessages
//            Log.d("debugging","fetchedChats: ${_fetchedChat.value.size}")
//        }
        Log.d("debugging","fetchedChats")
        _fetchedChat.value = repository.fetchChats(
            request = ApiRequest(
                userId = chatId.value
            )
        ).listMessages.reversed()
        Log.d("debugging","fetchedChats: ${_fetchedChat.value.size}")
        Log.d("debugging","fetchedChats: ${_fetchedChat.value.toString()}")
    }

    suspend fun getUserInfoByUserId() {
//        viewModelScope.launch(Dispatchers.IO) {
//            Log.d("debugging","getChatUser: ${repository.getUserInfoById(request = ApiRequest(userId = chatId.value)).user}")
//            _chatUser.value = repository.getUserInfoById(request = ApiRequest(userId = chatId.value)).user!!
//        }
        Log.d("debugging","getChatUser: ${repository.getUserInfoById(request = ApiRequest(userId = chatId.value)).user}")
        _chatUser.value = repository.getUserInfoById(request = ApiRequest(userId = chatId.value)).user!!
    }

//    fun addChat(currentUserId: String) {
//        if(chatText.value.isNotEmpty()) {
//            viewModelScope.launch(Dispatchers.IO) {
//                repository.addChats(request = ApiRequest(
//                    message = Message(
//                        author = currentUserId,
//                        receiver = listOf(chatUser.value.userId),
//                        messageText = chatText.value
//                    )
//                ))
//            }
//        }
//    }

}
package com.example.chatapp.presentation.screen.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.chatapp.data.remote.ChatSocketService
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.ChatEvent
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserItem
import com.example.chatapp.domain.repository.Repository
import com.example.chatapp.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class MainChatViewModel @Inject constructor(
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

    private val _isTyping = MutableStateFlow(false)
    val isTyping = _isTyping.asStateFlow()

    private var job: Job? = null

    private val jobs = ConcurrentHashMap<String, Pair<Job, UserItem?>>()

    init {
        viewModelScope.launch {
            _currentUser.collectLatest {
                if(it.userId != null) {
                    fetchUsers()
                    connectToChat()
                }
            }
        }

        viewModelScope.launch {
            _chatId.collectLatest { chatId ->
                if (chatId.isNotBlank()) {
                    getUserInfoByUserId()
                    fetchChats()
                    _chatUser.collectLatest { chatUser ->
                        if (chatUser.userId != null) {
                            setOnline()
                        }
                    }
                }
            }
        }
    }

    fun connectToChat() {
        viewModelScope.launch {
            Log.d("debugging2","userId: ${currentUser.value.userId} connected main")
            val result = currentUser.value.userId?.let { chatSocketService.initSession(it) }
            chatSocketService.sendOnline(true)
            when(result) {
                is RequestState.Success -> {
                    Log.d("debugging2","result is success")
                    viewModelScope.launch {
                        chatSocketService.observeChatEvent(viewModelScope)
                            .collectLatest { chatEvent ->
                                Log.d("debugging2","chatEvent: $chatEvent main")
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
                                    is ChatEvent.OnlineEvent -> {
                                        handleOnlineEvent(chatEvent)
                                    }
                                }
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

    private fun updateFetchedUserList(author: String, text: String, receiver: List<String?>, message: Boolean) {

        if(message) {
            val user: UserItem
            val newList: List<UserItem>

            if(author == currentUser.value.userId) {
                user = fetchedUser.value.find { it.userId == receiver[0] } ?: return
                newList = fetchedUser.value.filter { it.userId != receiver[0] }
            } else {
                user = fetchedUser.value.find { it.userId == author } ?: return
                newList = fetchedUser.value.filter { it.userId != author }
            }

            val newMessage = Message(
                author = author,
                messageText = text,
                receiver = receiver
            )

            val newUser = user.copy(
                lastMessage = newMessage
            )
            _fetchedUser.value = listOf(newUser) + newList
        } else {
            val newList = fetchedUser.value.toMutableList().apply {
                val index = if(author == currentUser.value.userId) {
                    indexOfFirst { it.userId == receiver[0] }
                } else {
                    indexOfFirst { it.userId == author }
                }
                if(index != -1){
                    val user = get(index)
                    val newUser = user.copy(
                        lastMessage = Message(
                            author = author,
                            messageText = text,
                            receiver = receiver
                        )
                    )
                    set(index,newUser)
                } else {
                    Log.d("message", "user not found in the list")
                }
            }
            _fetchedUser.value = newList
        }
    }

    private fun updateFetchedChatList(author: String, text: String, receiver: List<String?>) {
        val newMessage = Message(
            author = author,
            messageText = text,
            receiver = receiver
        )
        _fetchedChat.value = listOf(newMessage) + _fetchedChat.value
    }

    private suspend fun handleMessageEvent(chatEvent: ChatEvent.MessageEvent) {
        withContext(Dispatchers.IO) {

            // MAIN SCREEN
            val currentPair = jobs[chatEvent.receiverUserIds[0]]
            if (currentPair != null) {
                currentPair.first.cancel()
                jobs.remove(chatEvent.receiverUserIds[0])
            }
            updateFetchedUserList(chatEvent.receiverUserIds[0], chatEvent.messageText, listOf(currentUser.value.userId), true)

            // CHAT SCREEN
            if(chatId.value != "") {
                updateFetchedChatList(chatEvent.receiverUserIds[0], chatEvent.messageText, listOf(currentUser.value.userId))
            }
        }
    }

    private suspend fun handleTypingEvent(chatEvent: ChatEvent.TypingEvent) {
        withContext(Dispatchers.IO) {

            // MAIN SCREEN
            val receiverUserId = chatEvent.receiverUserIds[0]
            val user: UserItem?

            val currentPair = jobs[receiverUserId]
            if (currentPair != null) {
                user = currentPair.second
                currentPair.first.cancel()
            } else {
                user = fetchedUser.value.find { it.userId == receiverUserId }
            }

            updateFetchedUserList(receiverUserId, chatEvent.typingText, listOf(currentUser.value.userId), false)

            val jobCancel = viewModelScope.launch(Dispatchers.IO) {
                try {
                    delay(500)
                    user?.lastMessage?.let { lastMessage ->
                        if (lastMessage.author != null && lastMessage.messageText != null) {
                            updateFetchedUserList(lastMessage.author, lastMessage.messageText, lastMessage.receiver, false)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("typing", "Error in typing job: ${e.message}", e)
                } finally {
                    jobs.remove(receiverUserId)
                }
            }
            jobs[receiverUserId] = Pair(jobCancel, user)


            // CHAT SCREEN
            if(chatId.value != "") {
                job?.cancel()

                _isTyping.value = chatEvent.typingText == "typing..."

                job = viewModelScope.launch {
                    delay(500)
                    _isTyping.value = false
                }
            }
        }
    }

    private suspend fun handleListEvent(chatEvent: ChatEvent.ListEvent) {
        withContext(Dispatchers.IO) {

            // MAIN SCREEN
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
                )
                ).chat
            )
            _fetchedUser.value = listOf(newUserItem) + _fetchedUser.value
        }
    }

    private suspend fun handleOnlineEvent(chatEvent: ChatEvent.OnlineEvent) {
        withContext(Dispatchers.IO) {
            // CHAT SCREEN
            if(chatId.value != "") {
                _online.value = chatEvent.online
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
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
                if (currentUser != null) {
                    updateFetchedChatList(currentUser.userId.toString(), currentChatText, listOf(chatId.value))
                    updateFetchedUserList(currentUser.userId.toString(), currentChatText, listOf(chatId.value), true)
                }
                chatSocketService.sendMessage(message = currentChatText, receiverUserIds = listOf(chatId.value))
            }
        }
    }

    fun updateSearchQuery(query: String){
        _searchQuery.value = query
    }

    fun updateChatId(id: String){
        _chatId.value = id
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
                    )
                    ).chat
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
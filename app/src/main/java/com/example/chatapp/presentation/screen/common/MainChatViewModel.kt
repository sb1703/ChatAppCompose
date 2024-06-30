package com.example.chatapp.presentation.screen.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.chatapp.connectivity.ConnectivityObserver
import com.example.chatapp.connectivity.NetworkConnectivityObserver
import com.example.chatapp.data.remote.ChatSocketService
import com.example.chatapp.domain.model.ApiRequest
import com.example.chatapp.domain.model.ChatEvent
import com.example.chatapp.domain.model.FCMToken
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.NotificationBody
import com.example.chatapp.domain.model.SeenBy
import com.example.chatapp.domain.model.SendMessageDto
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserItem
import com.example.chatapp.domain.model.getCurrentTimeIn12HourFormat
import com.example.chatapp.domain.repository.Repository
import com.example.chatapp.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
    private val connectivity: NetworkConnectivityObserver,
    private val repository: Repository,
    private val chatSocketService: ChatSocketService
): ViewModel() {

    private val _network = MutableStateFlow(ConnectivityObserver.Status.Unavailable)
    val network = _network.asStateFlow()

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

    private val _lastLogin = MutableStateFlow("")
    val lastLogin = _lastLogin.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping = _isTyping.asStateFlow()

    private val _isFCMTokenSentToServer = MutableStateFlow(false)
    val isFCMTokenSentToServer = _isFCMTokenSentToServer.asStateFlow()

    private var job: Job? = null

    private val jobs = ConcurrentHashMap<String, Pair<Job, UserItem?>>()

    init {
        viewModelScope.launch {
            connectivity.observe().collectLatest {
                _network.value = it
            }
        }
    }

    suspend fun connectToChat() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = currentUser.value.userId?.let { chatSocketService.initSession(it) }
            when(result) {
                is RequestState.Success -> {
                    Log.d("debugging3","result is success")
                    viewModelScope.launch(Dispatchers.IO) {
                        chatSocketService.observeChatEvent()
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
                                    is ChatEvent.SeenEvent -> {
                                        handleSeenEvent(chatEvent)
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

    private fun updateFetchedUserList(author: String, text: String, receiver: List<String?>, message: Boolean, messageId: String = "") {

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
                messageId = messageId,
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
                        lastMessage = user.lastMessage?.let {
                            Message(
                                author = author,
                                messageText = text,
                                receiver = receiver,
                                seenBy = it.seenBy
                            )
                        } ?: Message(
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

    private fun updateFetchedChatList(author: String, text: String, receiver: List<String?>, messageId: String) {
        val newMessage = Message(
            messageId = messageId,
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
            updateFetchedUserList(chatEvent.receiverUserIds[0], chatEvent.messageText, listOf(currentUser.value.userId), true, chatEvent.messageId)

            // CHAT SCREEN
            if(chatId.value != "") {
                updateFetchedChatList(chatEvent.receiverUserIds[0], chatEvent.messageText, listOf(currentUser.value.userId), chatEvent.messageId)
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

    private suspend fun handleSeenEvent(chatEvent: ChatEvent.SeenEvent) {
        withContext(Dispatchers.IO) {
            // CHAT SCREEN
            if (chatId.value != "") {
                val newList = fetchedChat.value.toMutableList().apply {
                    forEachIndexed { index, message ->
                        if(message.messageId in chatEvent.messageIds) {
                            val newMessage = message.copy(
                                seenBy = message.seenBy.plus(SeenBy(
                                    seenAt = chatEvent.seenAt,
                                    userId = chatEvent.receiverUserIds[0]
                                ))
                            )
                            set(index, newMessage)
                        }
                    }
                }
                _fetchedChat.value = newList
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

//    override fun onCleared() {
//        super.onCleared()
//        disconnect()
//    }

    @OptIn(ExperimentalCoroutinesApi::class)
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
                    val messageEntity = Message(
                        author = currentUser.userId.toString(),
                        messageText = currentChatText,
                        receiver = listOf(chatId.value)
                    )
                    val response = viewModelScope.async(Dispatchers.IO) {
                        repository.addChats(
                            request = ApiRequest(
                                message = messageEntity
                            )
                        )
                    }
                    response.invokeOnCompletion {
                        if(it == null) {
                            val messageId = response.getCompleted().messageId
                            viewModelScope.launch {
                                updateFetchedChatList(currentUser.userId.toString(), currentChatText, listOf(chatId.value), messageId)
                                updateFetchedUserList(currentUser.userId.toString(), currentChatText, listOf(chatId.value), true, messageId)
                                chatSocketService.sendMessage(message = currentChatText, receiverUserIds = listOf(chatId.value), messageId = messageId)
                                sendMessageNotification(currentChatText)
                            }
                        }
                    }
                }
            }
        }
    }

    fun sendSeen() {
        viewModelScope.launch {
            val messageIds = fetchedChat.value.filter {
                it.author == chatId.value && it.seenBy.none { it.userId == currentUser.value.userId }
            }.mapNotNull {
                it.messageId
            }

            if(messageIds.isEmpty()) return@launch
            chatSocketService.sendSeen(receiverUserIds = listOf(chatId.value), messageIds = messageIds, seenAt = getCurrentTimeIn12HourFormat())
            val newList = fetchedChat.value.toMutableList().apply {
                forEachIndexed { index, message ->
                    if(message.messageId in messageIds) {
                        val newMessage = message.copy(
                            seenBy = message.seenBy.plus(SeenBy(
                                seenAt = getCurrentTimeIn12HourFormat(),
                                userId = currentUser.value.userId.toString()
                            ))
                        )
                        set(index, newMessage)
                    }
                }
            }

            _fetchedChat.value = newList
        }
    }

    fun updateSearchQuery(query: String){
        _searchQuery.value = query
    }

    fun updateChatId(id: String){
        _chatId.value = id
    }

    suspend fun fetchUsers(){
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

    suspend fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentUser.value = repository.getUserInfo().user!!
        }
    }

    suspend fun getOnlineStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            _online.value = repository.getOnlineStatus(ApiRequest(
                userId = chatId.value
            )).online
        }
    }

    suspend fun getLastLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            _lastLogin.value = repository.getLastLogin(ApiRequest(
                userId = chatId.value
            )).lastLogin.toString()
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

    fun setIsFCMTokenSentToServerToTrue() {
        _isFCMTokenSentToServer.value = true
    }

    suspend fun fetchChats(){
        viewModelScope.launch(Dispatchers.IO) {
            _fetchedChat.value = repository.fetchChats(
                request = ApiRequest(
                    userId = chatId.value
                )
            ).listMessages.reversed()
        }
    }

    suspend fun getUserInfoByUserId() {
        viewModelScope.launch(Dispatchers.IO) {
            _chatUser.value = repository.getUserInfoById(request = ApiRequest(userId = chatId.value)).user!!
        }
    }

    fun sendMessageNotification(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // CHAT SCREEN
            val fcmToken = chatUser.value.fcmToken
            if(chatId.value != "" && fcmToken != null) {
                val messageDto = SendMessageDto(
                    // for chatId get token
                    to = fcmToken.token,
                    notification = NotificationBody(
                        userId = currentUser.value.userId ?: "1",
                        title = currentUser.value.name,
                        body = text,
                        profilePhotoUri = currentUser.value.profilePhoto
                    )
                )

                try {
                    repository.sendMessageNotification(messageDto)
                } catch (e: Exception) {
                    Log.e("notification", "Error in sending notification: ${e.message}", e)
                }
            }
        }
    }

    suspend fun updateFCMTokenServer() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.readFCMTokenState().collectLatest { token ->
                repository.updateFCMToken(request = ApiRequest(
                    fcmToken = FCMToken(
                        token = token
                    )
                )
                )
            }
        }
    }

}
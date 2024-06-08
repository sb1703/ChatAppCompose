package com.example.chatapp.presentation.screen.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.remote.ChatSocketService
import com.example.chatapp.domain.model.ChatEvent
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserUpdate
import com.example.chatapp.domain.repository.Repository
import com.example.chatapp.util.RequestState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val chatSocketService: ChatSocketService
): ViewModel() {

    private val _nameQuery = MutableStateFlow("")
    val nameQuery = _nameQuery.asStateFlow()

    private val _isReadOnly = MutableStateFlow(true)
    val isReadOnly = _isReadOnly.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    init {
        getCurrentUser()
        viewModelScope.launch {
            _currentUser.collectLatest {
                if (it.userId != null) {
                    updateName(currentUser.value.name)
//                    connectToChat()
                }
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = repository.getUserInfo().user!!
        }
    }

    fun connectToChat() {
        viewModelScope.launch {
            Log.d("debugging2","userId: ${currentUser.value.userId} connected profile")
            val result = currentUser.value.userId?.let { chatSocketService.initSession(it) }
            when(result) {
                is RequestState.Success -> {
                    Log.d("debugging2","result is success")
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

    fun updateName(query: String) {
        _nameQuery.value = query
    }

    fun toggleIsReadOnly() {
        _isReadOnly.value = !_isReadOnly.value
    }

    fun updateUser() {
        if(!isReadOnly.value && nameQuery.value.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateUser(
                    userUpdate = UserUpdate(
                        name = nameQuery.value
                    )
                )
            }
        }
    }

    fun setOnlineFalse() {
        viewModelScope.launch {
            chatSocketService.sendOnline(false)
        }
    }

    fun disconnect() {
        Log.d("disconnect","disconnecting profile")
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    override fun onCleared() {
        super.onCleared()
//        Log.d("disconnect","onCleared profile")
//        disconnect()
    }

}
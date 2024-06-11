package com.example.chatapp.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.connectivity.ConnectivityObserver
import com.example.chatapp.connectivity.NetworkConnectivityObserver
import com.example.chatapp.data.remote.ChatSocketService
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserUpdate
import com.example.chatapp.domain.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val connectivity: NetworkConnectivityObserver,
    private val repository: Repository,
    private val chatSocketService: ChatSocketService
): ViewModel() {

    private val _network = MutableStateFlow(ConnectivityObserver.Status.Unavailable)
    val network = _network.asStateFlow()

    private val _nameQuery = MutableStateFlow("")
    val nameQuery = _nameQuery.asStateFlow()

    private val _isReadOnly = MutableStateFlow(true)
    val isReadOnly = _isReadOnly.asStateFlow()

    private val _currentUser = MutableStateFlow(User())
    val currentUser = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            connectivity.observe().collectLatest {
                _network.value = it
            }
        }
    }

    suspend fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentUser.value = repository.getUserInfo().user!!
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

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

//    override fun onCleared() {
//        super.onCleared()
//        disconnect()
//    }

}
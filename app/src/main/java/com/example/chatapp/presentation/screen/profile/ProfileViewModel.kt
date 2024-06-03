package com.example.chatapp.presentation.screen.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.model.User
import com.example.chatapp.domain.model.UserUpdate
import com.example.chatapp.domain.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

//    private val _nameQuery = mutableStateOf("")
//    val nameQuery = _nameQuery

    private val _nameQuery = MutableStateFlow("")
    val nameQuery = _nameQuery.asStateFlow()

//    private val _currentUser = mutableStateOf(User())
//    val currentUser = _currentUser

//    private val _currentUser = MutableStateFlow(User())
//    val currentUser = _currentUser.asStateFlow()

//    private val _isReadOnly = mutableStateOf(true)
//    val isReadOnly = _isReadOnly

    private val _isReadOnly = MutableStateFlow(true)
    val isReadOnly = _isReadOnly.asStateFlow()

//    init {
//        getCurrentUser()
//        updateName(currentUser.value.name)
//    }

    fun updateName(query: String) {
        _nameQuery.value = query
    }

    fun toggleIsReadOnly() {
        _isReadOnly.value = !_isReadOnly.value
    }

//    fun getCurrentUser() {
//        viewModelScope.launch(Dispatchers.IO) {
//            _currentUser.value = repository.getUserInfo().user!!
//        }
//    }

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

}
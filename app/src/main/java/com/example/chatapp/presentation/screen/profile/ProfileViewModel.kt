package com.example.chatapp.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _nameQuery = MutableStateFlow("")
    val nameQuery = _nameQuery.asStateFlow()

    private val _isReadOnly = MutableStateFlow(true)
    val isReadOnly = _isReadOnly.asStateFlow()

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

}
package com.example.chatapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreOperations {
    suspend fun saveSignedInState(signedIn: Boolean)
    fun readSignedInState(): Flow<Boolean>
    suspend fun saveFCMTokenState(token: String)
    fun readFCMTokenState(): Flow<String>
}
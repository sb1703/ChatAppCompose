package com.example.chatapp.data.remote

import android.util.Log
import com.example.chatapp.domain.repository.Repository
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService: FirebaseMessagingService() {

    @Inject
    lateinit var repository: Repository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(serviceJob + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Update server
        Log.d("PushNotificationService", "New token: $token")

        serviceScope.launch {
            repository.saveFCMTokenState(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Respond to received messages
        Log.d("PushNotificationService", "Message received: ${message.notification?.body}")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the job when the service is destroyed to prevent memory leaks
        serviceJob.cancel()
    }

}
package com.example.chatapp.data.remote

import android.Manifest
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.chatapp.MainActivity
import com.example.chatapp.domain.repository.Repository
import com.example.chatapp.receiver.MyReceiver
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationService: FirebaseMessagingService() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

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
        Log.d("PushNotificationService", "Message received: ${message.data}")

        if (message.data.isNotEmpty()) {
            val userId = message.data["userId"] ?: "1"
            val title = message.data["title"] ?: "New Message"
            val body = message.data["body"] ?: "You have received a new message."
            val profilePicUrl = message.data["profilePhotoUri"] ?: ""

            if (isAppInForeground()) {
                // Do not show notification if app is in foreground
                Log.d("PushNotificationService", "App is in foreground, not showing notification.")
            } else {
                // Show custom notification if app is in background or closed
                showNotification(userId, title, body, profilePicUrl)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun showNotification(userId: String, title: String, body: String, profilePicUrl: String) {
        val bitmap = serviceScope.async {
            getBitmap(profilePicUrl)
        }
        bitmap.invokeOnCompletion {
            if(it == null) {

//                val intent = Intent(this, MyReceiver::class.java).apply {
//                    putExtra("MESSAGE", "Clicked!")
//                }
//                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
                val clickIntent = Intent(this, MainActivity::class.java)
                val clickPendingIntent = PendingIntent.getActivity(this, 1, clickIntent, PendingIntent.FLAG_IMMUTABLE)

                val notificationId = userId.hashCode()
                val notification = notificationBuilder
                    .setContentTitle(title)
                    .setContentText(body)
                    .setLargeIcon(bitmap.getCompleted())
//                    .addAction(0,"ACTION", pendingIntent)
                    .setContentIntent(clickPendingIntent)
                    .build()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!hasPermission) {
                        ActivityCompat.requestPermissions(
                            MainActivity(),
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            0
                        )
                    }
                }

                Log.d("PushNotificationService", "Showing notification.")
                notificationManager.notify(notificationId, notification)
            }
        }
    }

    private suspend fun getBitmap(profilePicUrl: String): Bitmap {
        val loading = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(profilePicUrl)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            if (appProcess.processName == packageName &&
                appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the job when the service is destroyed to prevent memory leaks
        serviceJob.cancel()
    }

}
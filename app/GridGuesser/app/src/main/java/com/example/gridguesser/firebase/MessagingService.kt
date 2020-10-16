package com.example.gridguesser.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.gridguesser.MainActivity
import com.example.gridguesser.R
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.deviceID.DeviceID
import com.example.gridguesser.http.ServerInteractions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val TAG = "GridGuesser"
const val NOTIFICATION_CHANNEL_ID = "10101"
class MessagingService: FirebaseMessagingService() {
    private var gameRepo: GameRepository = GameRepository.get()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            handleNow(remoteMessage.data)
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(
                TAG,
                "Message Notification: " + remoteMessage.notification
            )

            remoteMessage.notification!!.body?.let { remoteMessage.notification!!.title?.let { it1 ->
                sendNotification(
                    it,
                    it1
                )
            } }

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun handleNow(data: MutableMap<String, String>) {
        val id = data["id"]
        when (data["event"]){
            "join" -> {
                val username = data["username"]
                if (id != null && username != null) {
                    gameRepo.updateUserName(id, username)
                }
            }
            "finish" -> {
                if (id != null) {
                    gameRepo.finishGame(id)
                }
            }
            "turn" -> {
                val state = data["state"]
                if(id != null && state != null && state == "3"){
                    gameRepo.updateScore(id, false)
                }
                if (id != null) {
                    gameRepo.alternateTurn(id)
                }
            }
            "board" -> {
                if(id != null){
                    gameRepo.incStatus(id)
                }
            }
            else -> {
                Log.d(TAG, "FOUND: ${data["event"]}")
            }
        }
        data["event"]?.let {
            if (id != null) {
                gameRepo.updateEvent(it, id)
                gameRepo.notifyChange(id, true)
            }
        }
    }

    private fun sendRegistrationToServer(token: String?) {
        val serverInteractions = ServerInteractions.get()
        if (token != null) {
            serverInteractions.updateUser(DeviceID.getDeviceID(contentResolver), gameRepo.currentSettings.username, token)
        }
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun sendNotification(messageBody: String, title: String) {
        Log.d(TAG, "$title: $messageBody")
        val intent = MainActivity.newIntent(this)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = NOTIFICATION_CHANNEL_ID
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }


        notificationManager.notify(0, notificationBuilder.build())
    }

}

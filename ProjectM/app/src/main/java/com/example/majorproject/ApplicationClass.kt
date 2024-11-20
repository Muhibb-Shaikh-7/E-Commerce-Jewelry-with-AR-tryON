package com.example.majorproject

import android.app.Application
import android.util.Log
import com.example.majorproject.notification.NotificationHelper

class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()

        val notificationHelper = NotificationHelper()
        notificationHelper.createChannel(this) // Ensure the channel is created before sending notifications

        // Try sending a notification
        try {
            notificationHelper.sendNotification(
                applicationContext,
                "New Message",
                "You have a new message waiting!",
                R.drawable.banner4
            )
        } catch (e: Exception) {
            Log.e("ApplicationClass", "Error sending notification", e)
        }
    }
}

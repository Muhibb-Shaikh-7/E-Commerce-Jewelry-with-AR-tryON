package com.example.majorproject

import android.app.Application
import com.example.majorproject.notification.NotificationHelper

class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize the NotificationHelper
        // Inside your ApplicationClass or any other activity/fragment

        val notificationHelper = NotificationHelper()

// Send a general notification with a specific image
        notificationHelper.sendNotification(
            applicationContext,
            "New Message",
            "You have a new message waiting!",
            R.drawable.banner4 // Image for the notification
        )

// Send a promotional notification with a different image
        notificationHelper.sendNotification(
            applicationContext,
            "Special Offer",
            "Get 20% off your next purchase!",
            R.drawable.banner3 // Different image for this notification
        )


    }
}

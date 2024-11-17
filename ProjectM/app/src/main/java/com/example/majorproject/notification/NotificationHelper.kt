package com.example.majorproject.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.majorproject.R

class NotificationHelper {

    // Notification channel ID
    private val notificationId = "mahavir_channel"

    // Method to create a notification channel (required for API 26 and above)
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Mahavir Channel"
            val channelDescription = "Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            // Create the notification channel
            val channel = NotificationChannel(notificationId, channelName, importance).apply {
                description = channelDescription
            }

            // Register the channel with the system (this must be done once)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Method to send a notification with a custom layout and dynamic image
    fun sendNotification(context: Context, notificationType: String, notificationContent: String, imageResId: Int) {
        // Generate a unique ID for the notification based on the notification type or content
        val uniqueNotificationId = notificationType.hashCode() // You can customize this ID generation

        // Create a custom layout for the notification with dynamic image
        val remoteViews = RemoteViews(context.packageName, R.layout.notification_img_layout)
        remoteViews.setImageViewResource(R.id.notification_image, imageResId) // Set dynamic image for each notification

        // Create a notification using NotificationCompat
        val builder = NotificationCompat.Builder(context, notificationId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Set your small notification icon (white)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()) // Use custom layout
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the priority of the notification
            .setAutoCancel(true) // Dismiss the notification when clicked
            .setCustomContentView(remoteViews) // Set the custom RemoteViews layout
            .setContentText(notificationContent) // Optionally set the content text or message
            .setContentTitle(notificationType) // Title (optional)

        // Get the NotificationManagerCompat to show the notification
        val notificationManager = NotificationManagerCompat.from(context)

        // Check if permission is granted before posting the notification
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Notify the user with a unique notification ID
        notificationManager.notify(uniqueNotificationId, builder.build())
    }

    fun cancelNotification(context: Context, notificationType: String) {
        val uniqueNotificationId = notificationType.hashCode() // The same ID that was used to post the notification

        // Get the NotificationManagerCompat to cancel the notification
        val notificationManager = NotificationManagerCompat.from(context)

        // Cancel the notification by its ID
        notificationManager.cancel(uniqueNotificationId)
    }
}

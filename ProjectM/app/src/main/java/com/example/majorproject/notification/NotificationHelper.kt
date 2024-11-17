package com.example.majorproject.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.majorproject.R

class NotificationHelper {

    private val notificationId = "mahavir_channel"
    private val TAG = "NotificationHelper"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Mahavir Channel"
            val channelDescription = "Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(notificationId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }

    fun sendNotification(context: Context, notificationType: String, notificationContent: String, imageResId: Int) {
        val uniqueNotificationId = notificationType.hashCode()

        val remoteViews = RemoteViews(context.packageName, R.layout.notification_img_layout)
        remoteViews.setImageViewResource(R.id.notification_image, imageResId)

        val builder = NotificationCompat.Builder(context, notificationId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setCustomContentView(remoteViews)
            .setContentText(notificationContent)
            .setContentTitle(notificationType)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(uniqueNotificationId, builder.build())
        Log.d(TAG, "Notification sent: $notificationType")
    }

    fun cancelNotification(context: Context, notificationType: String) {
        val uniqueNotificationId = notificationType.hashCode()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(uniqueNotificationId)
        Log.d(TAG, "Notification canceled: $notificationType")
    }
}

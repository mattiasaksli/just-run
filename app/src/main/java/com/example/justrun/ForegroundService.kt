package com.example.justrun

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class ForegroundService : Service() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("run_service", "Running app background service")
            } else {
                // If earlier version channel ID is not used
                ""
            }

        val notification: Notification = Notification.Builder(this, channelId)
            .setContentTitle("Just Run")
            .setLargeIcon(BitmapFactory.decodeResource(resources, android.R.drawable.ic_dialog_email))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentText("We're stealing your data, please don't turn this off!")
            .build()
        startForeground(2001, notification)

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

}
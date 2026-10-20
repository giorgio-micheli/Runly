package io.github.giorgiomicheli.run.presentation.active_run.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import io.github.giorgiomicheli.core.presentation.designsystem.R

class ActiveRunService: Service() {
    // We need the context to create a notificationManager, at this point the context is not yet constructed
    // This way this block of code will be executed the first time the variable is being accessed
    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    private val baseNotification by lazy {
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(getString(io.github.giorgiomicheli.run.presentation.R.string.active_run))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun start(activityClass: Class<*>) {
        if (!isServiceActive) {
            isServiceActive = true
            createNotificationChannel()

            /*
            If we just use an intent, when the user will press on the foreground service's notification
            he will be redirected to the MainActivity, instead we want the user to be redirected immediately
            to the ActiveRunScreen. To accomplish that we use a mechanism called "deep links"
             */
            val activityIntent = Intent(applicationContext, activityClass).apply {
                data = "runly://active_run".toUri()
                // If the ActiveRunScreen is already at the top of the backstack, we won't launch another new instance
                // but we'll open the already existing instance
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            // We need a pending intent
            val pendingIntent = TaskStackBuilder.create(applicationContext).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }
            val notification = baseNotification
                .setContentText("00:00:00")
                .setContentIntent(pendingIntent)
                .build()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(io.github.giorgiomicheli.run.presentation.R.string.active_run),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }
    companion object {
        /*
            This boolean remains active until the run is active.
            A run is active from the click on "start run" to "finish run" even if it's paused.
            That's the difference with the other booleans like isTracking
         */
        var isServiceActive = false
        private const val CHANNEL_ID = "active_run"
    }
}
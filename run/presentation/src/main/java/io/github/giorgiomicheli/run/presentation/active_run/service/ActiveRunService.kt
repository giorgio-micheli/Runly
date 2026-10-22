package io.github.giorgiomicheli.run.presentation.active_run.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import io.github.giorgiomicheli.core.presentation.designsystem.R
import io.github.giorgiomicheli.core.presentation.ui.formatted
import io.github.giorgiomicheli.run.domain.RunningTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

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
    // This koin function provides us a reference to the singleton RunningTracker
    private val runningTracker by inject<RunningTracker>()

    /*
    Whenever we have a specific component in our app that has a particular lifetime, like a service,
    it's useful to create a custom coroutine scope binded to its lifetime.
    SupervisorJob makes sure that if there are multiple coroutines running in this coroutine scope
    and one of them fails, then this fail won't have an impact on the other ones.
     */
    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> {
                val activityClass = intent.getStringExtra(EXTRA_ACTIVITY_CLASS)
                    ?: throw IllegalArgumentException("No activity class provided")
                start(Class.forName(activityClass))
            }
            ACTION_STOP -> stop()
        }
        return START_STICKY
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
            // Here we must use and id starting from 1 not from zero
            startForeground(1, notification)
            updateNotification()
        }
    }

    fun stop() {
        stopSelf()
        isServiceActive = false
        // We cancel all the coroutines running in the serviceScope
        serviceScope.cancel()
        // We need to reinitialize the coroutine scope binded to this service, cause after invoking
        // cancel we can't reuse again the same CoroutineScope object. We need to create a new one.
        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    private fun updateNotification() {
        // The intermediate operator 'onEach' is creating a new cold flow here,
        // that's why we also have to launch it in an appropriate coroutineScope.
        runningTracker.elapsedTime.onEach { elapsedTime ->
            val notification = baseNotification
                .setContentText(elapsedTime.formatted())
                .build()

            notificationManager.notify(1, notification)
        }.launchIn(serviceScope)
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
        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"
        private const val EXTRA_ACTIVITY_CLASS = "EXTRA_ACTIVITY_CLASS"

        fun createStartIntent(context: Context, activityClass: Class<*>): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ACTIVITY_CLASS, activityClass.name)
            }
        }

        fun createStopIntent(context: Context): Intent {
            return Intent(context, ActiveRunService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }
}
package hu.pufffissal.countdownevents.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val REMINDERS = "reminders"

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            REMINDERS,
            "Emlékeztetők",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Események előtti értesítések"
        }
        nm.createNotificationChannel(channel)
    }
}


package hu.pufffissal.countdownevents.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import hu.pufffissal.countdownevents.MainActivity
import hu.pufffissal.countdownevents.R
import hu.pufffissal.countdownevents.data.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {
    @Inject lateinit var repo: EventRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_REMINDER) return
        val eventId = intent.getLongExtra(EXTRA_EVENT_ID, 0L)
        if (eventId == 0L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                NotificationChannels.ensure(context)
                val event = repo.getEvent(eventId)
                val title = event?.name ?: "Esemény"
                val content = "Közeleg: $title"

                val openIntent = Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("open_event_id", eventId)
                }
                val flags = android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                    (if (android.os.Build.VERSION.SDK_INT >= 23) android.app.PendingIntent.FLAG_IMMUTABLE else 0)
                val pi = androidx.core.app.TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(openIntent)
                    .getPendingIntent(eventId.toInt(), flags)

                val notif = NotificationCompat.Builder(context, NotificationChannels.REMINDERS)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .build()

                NotificationManagerCompat.from(context).notify(eventId.toInt(), notif)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_REMINDER = "hu.pufffissal.countdownevents.ACTION_REMINDER"
        const val EXTRA_EVENT_ID = "event_id"
    }
}


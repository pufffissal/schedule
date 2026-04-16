package hu.pufffissal.countdownevents.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetUpdateScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager: AlarmManager? = context.getSystemService(AlarmManager::class.java)

    fun scheduleNext() {
        val am = alarmManager ?: return
        val now = System.currentTimeMillis()
        val nextMinute = ((now / 60_000L) + 1L) * 60_000L
        val pi = pendingIntent()

        if (Build.VERSION.SDK_INT >= 23) {
            if (Build.VERSION.SDK_INT >= 31 && !am.canScheduleExactAlarms()) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextMinute, pi)
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextMinute, pi)
            }
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP, nextMinute, pi)
        }
    }

    fun cancel() {
        val am = alarmManager ?: return
        val pi = pendingIntent()
        am.cancel(pi)
        pi.cancel()
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, WidgetUpdateReceiver::class.java)
            .setAction(WidgetUpdateReceiver.ACTION_WIDGET_TICK)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        return PendingIntent.getBroadcast(context, 9001, intent, flags)
    }
}


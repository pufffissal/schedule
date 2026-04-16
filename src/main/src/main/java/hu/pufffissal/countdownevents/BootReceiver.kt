package hu.pufffissal.countdownevents

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import hu.pufffissal.countdownevents.widgets.WidgetUpdateScheduler
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var widgetScheduler: WidgetUpdateScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            widgetScheduler.scheduleNext()
        }
    }
}


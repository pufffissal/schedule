package hu.pufffissal.countdownevents.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetUpdateReceiver : BroadcastReceiver() {
    @Inject lateinit var refresher: WidgetRefresher
    @Inject lateinit var scheduler: WidgetUpdateScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_WIDGET_TICK) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                refresher.refreshAll()
                scheduler.scheduleNext()
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_WIDGET_TICK = "hu.pufffissal.countdownevents.ACTION_WIDGET_TICK"
    }
}


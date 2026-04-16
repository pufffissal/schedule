package hu.pufffissal.countdownevents

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import hu.pufffissal.countdownevents.util.ActivityProvider
import hu.pufffissal.countdownevents.widgets.WidgetUpdateScheduler
import hu.pufffissal.countdownevents.widgets.WidgetWorkScheduler

@HiltAndroidApp
class CountdownEventsApp : Application() {
    @Inject lateinit var activityProvider: ActivityProvider
    @Inject lateinit var widgetUpdateScheduler: WidgetUpdateScheduler
    @Inject lateinit var widgetWorkScheduler: WidgetWorkScheduler

    override fun onCreate() {
        super.onCreate()
        // kick off widget ticks (AlarmManager exact if possible, fallback otherwise)
        widgetUpdateScheduler.scheduleNext()
        widgetWorkScheduler.ensure()
    }
}


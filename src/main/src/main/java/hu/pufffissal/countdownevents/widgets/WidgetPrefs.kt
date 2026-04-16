package hu.pufffissal.countdownevents.widgets

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetPrefs @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs = context.getSharedPreferences("widgets", Context.MODE_PRIVATE)

    fun setEventIdForWidget(appWidgetId: Int, eventId: Long) {
        prefs.edit().putLong("event_$appWidgetId", eventId).apply()
    }

    fun getEventIdForWidget(appWidgetId: Int): Long? {
        val v = prefs.getLong("event_$appWidgetId", 0L)
        return if (v == 0L) null else v
    }

    fun deleteWidget(appWidgetId: Int) {
        prefs.edit().remove("event_$appWidgetId").apply()
    }
}


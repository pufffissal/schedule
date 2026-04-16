package hu.pufffissal.countdownevents.widgets

import android.content.Context
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetRefresher @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun refreshAll() {
        // Trigger updates for all widget types.
        CountdownSmallWidget().updateAll(context)
        CountdownWideWidget().updateAll(context)
        CountdownMediumWidget().updateAll(context)
    }
}


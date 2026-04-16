package hu.pufffissal.countdownevents.widgets

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import dagger.hilt.android.EntryPointAccessors
import hu.pufffissal.countdownevents.MainActivity
import hu.pufffissal.countdownevents.R
import hu.pufffissal.countdownevents.time.countdownParts

class CountdownWideWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        val ep = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
        val manager = GlanceAppWidgetManager(context)
        val appWidgetId = manager.getAppWidgetId(id)
        val eventId = ep.prefs().getEventIdForWidget(appWidgetId)
        val event = if (eventId != null) ep.repo().getEvent(eventId) else null
        val parts = if (event != null) countdownParts(System.currentTimeMillis(), event.targetEpochMillis) else null

        provideContent {
            widgetAccentSurface(accentArgb = event?.accentArgb) {
                if (eventId == null) {
                    widgetTitle("Válassz eseményt")
                    widgetSpacer8()
                    widgetSubtitle("Szerkeszd a widgetet")
                } else {
                    val e = event
                    if (e == null || parts == null) {
                        widgetTitle("Nincs esemény")
                        widgetSubtitle("Törölve lett?")
                    } else {
                        Column(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .clickable(
                                    actionStartActivity<MainActivity>(
                                        actionParametersOf(WidgetActions.EventIdKey to e.id)
                                    )
                                )
                        ) {
                            Row(
                                modifier = GlanceModifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    provider = ImageProvider(R.drawable.ic_widget_calendar),
                                    contentDescription = null,
                                    modifier = GlanceModifier.size(20.dp)
                                )
                                Spacer(modifier = GlanceModifier.width(8.dp))
                                widgetTitle(e.name)
                            }
                            widgetSpacer10()
                            widgetCountdown(
                                if (parts.isCompleted) {
                                    "Befejezett"
                                } else {
                                    "${parts.daysAbs} : ${parts.hoursAbs} : ${parts.minutesAbs} : ${parts.secondsAbs}"
                                }
                            )
                            Spacer(modifier = GlanceModifier.height(2.dp))
                            widgetSubtitle("N : Ó : P : M")
                        }
                    }
                }
            }
        }
    }
}

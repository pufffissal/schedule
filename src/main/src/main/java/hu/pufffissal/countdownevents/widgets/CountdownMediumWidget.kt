package hu.pufffissal.countdownevents.widgets

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider
import dagger.hilt.android.EntryPointAccessors
import hu.pufffissal.countdownevents.MainActivity
import hu.pufffissal.countdownevents.R
import hu.pufffissal.countdownevents.time.countdownParts

class CountdownMediumWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        val ep = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
        val events = ep.repo().getNextEvents(2)

        provideContent {
            widgetRoundedPanel {
                if (events.isEmpty()) {
                    widgetTitle("Nincs esemény")
                    widgetSpacer8()
                    widgetSubtitle("Adj hozzá a listában")
                } else {
                    Column(modifier = GlanceModifier.fillMaxWidth()) {
                        widgetTitle("Következő")
                        widgetSpacer10()
                        events.forEachIndexed { idx, e ->
                            val parts = countdownParts(System.currentTimeMillis(), e.targetEpochMillis)
                            val stripe = e.accentArgb?.let { ColorProvider(Color(it)) }
                                ?: ColorProvider(Color(0xFF444444))
                            Row(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .clickable(
                                        actionStartActivity<MainActivity>(
                                            actionParametersOf(WidgetActions.EventIdKey to e.id)
                                        )
                                    )
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = GlanceModifier
                                        .width(4.dp)
                                        .height(40.dp)
                                        .background(stripe)
                                ) {}
                                Spacer(modifier = GlanceModifier.width(10.dp))
                                Image(
                                    provider = ImageProvider(R.drawable.ic_widget_calendar),
                                    contentDescription = null,
                                    modifier = GlanceModifier.size(18.dp)
                                )
                                Spacer(modifier = GlanceModifier.width(8.dp))
                                Column(modifier = GlanceModifier.fillMaxWidth()) {
                                    widgetTitle(e.name)
                                    widgetSubtitle(
                                        if (parts.isCompleted) "Befejezett"
                                        else "${parts.daysAbs}n ${parts.hoursAbs}ó ${parts.minutesAbs}p ${parts.secondsAbs}mp"
                                    )
                                }
                            }
                            if (idx == 0 && events.size > 1) {
                                Spacer(modifier = GlanceModifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


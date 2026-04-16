package hu.pufffissal.countdownevents.widgets

import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.compose.runtime.Composable
import androidx.glance.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.unit.dp

val WidgetBg = ColorProvider(Color(0xCC1C1C1C))
val WidgetTitle = ColorProvider(Color.White)
val WidgetSubtitle = ColorProvider(Color(0xFF888888))
private val WidgetAccentFallback = ColorProvider(Color(0xFF444444))

@Composable
fun widgetRoundedPanel(content: @Composable () -> Unit) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(18.dp)
            .background(WidgetBg)
            .padding(14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        content()
    }
}

@Composable
fun widgetAccentSurface(accentArgb: Int?, content: @Composable () -> Unit) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(18.dp)
            .background(WidgetBg)
    ) {
        Row(modifier = GlanceModifier.fillMaxSize()) {
            val stripe = accentArgb?.let { ColorProvider(Color(it)) } ?: WidgetAccentFallback
            Box(
                modifier = GlanceModifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(stripe)
            ) {}
            Box(
                modifier = GlanceModifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                content()
            }
        }
    }
}

@Composable
fun widgetTitle(text: String) = Text(
    text = text,
    style = TextStyle(color = WidgetTitle, fontWeight = FontWeight.Medium)
)

@Composable
fun widgetCountdown(text: String) = Text(
    text = text,
    style = TextStyle(color = WidgetTitle, fontWeight = FontWeight.Bold)
)

@Composable
fun widgetSubtitle(text: String) = Text(
    text = text,
    style = TextStyle(color = WidgetSubtitle)
)

@Composable
fun widgetSpacer8() = Spacer(modifier = GlanceModifier.height(8.dp))

@Composable
fun widgetSpacer10() = Spacer(modifier = GlanceModifier.height(10.dp))

package hu.pufffissal.countdownevents

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import hu.pufffissal.countdownevents.ui.App
import hu.pufffissal.countdownevents.ui.theme.CountdownTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var openEventIdFromIntent by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openEventIdFromIntent = extractOpenEventId(intent)
        setContent {
            val openId = openEventIdFromIntent
            CountdownTheme {
                App(initialOpenEventId = openId)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openEventIdFromIntent = extractOpenEventId(intent)
    }

    private fun extractOpenEventId(intent: Intent?): Long? {
        val extras = intent?.extras ?: return null
        if (!extras.containsKey("event_id")) return null
        val id = extras.getLong("event_id", 0L)
        return id.takeIf { it != 0L }
    }
}

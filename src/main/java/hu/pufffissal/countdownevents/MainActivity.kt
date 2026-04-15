package hu.pufffissal.countdownevents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import hu.pufffissal.countdownevents.ui.App
import hu.pufffissal.countdownevents.ui.theme.CountdownTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountdownTheme {
                App()
            }
        }
    }
}


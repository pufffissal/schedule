package hu.pufffissal.countdownevents.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import hu.pufffissal.countdownevents.ui.screens.EventsScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = "events",
        modifier = modifier,
        enterTransition = {
            fadeIn() + slideInHorizontally(initialOffsetX = { it / 6 })
        },
        exitTransition = {
            fadeOut() + slideOutHorizontally(targetOffsetX = { -it / 6 })
        },
        popEnterTransition = {
            fadeIn() + slideInHorizontally(initialOffsetX = { -it / 6 })
        },
        popExitTransition = {
            fadeOut() + slideOutHorizontally(targetOffsetX = { it / 6 })
        }
    ) {
        composable("events") {
            EventsScreen(
                onAdd = { /* TODO */ }
            )
        }
    }
}


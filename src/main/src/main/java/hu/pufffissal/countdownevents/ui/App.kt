package hu.pufffissal.countdownevents.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import hu.pufffissal.countdownevents.ui.screens.EventsScreen
import hu.pufffissal.countdownevents.ui.screens.SettingsScreen
import hu.pufffissal.countdownevents.ui.screens.UpsertEventScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun App(
    initialOpenEventId: Long? = null,
    modifier: Modifier = Modifier
) {
    val navController = rememberAnimatedNavController()

    LaunchedEffect(initialOpenEventId) {
        val id = initialOpenEventId ?: return@LaunchedEffect
        if (id == 0L) return@LaunchedEffect
        navController.navigate("edit/$id") {
            launchSingleTop = true
        }
    }

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
                onAdd = { navController.navigate("add") },
                onOpenEvent = { id -> navController.navigate("edit/$id") },
                onOpenSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable("add") {
            UpsertEventScreen(
                eventId = null,
                onDone = { navController.popBackStack() }
            )
        }
        composable(
            route = "edit/{eventId}",
            arguments = listOf(
                navArgument("eventId") { type = NavType.LongType }
            )
        ) { entry ->
            val raw = entry.arguments?.getLong("eventId") ?: 0L
            UpsertEventScreen(
                eventId = raw.takeIf { it != 0L },
                onDone = { navController.popBackStack() }
            )
        }
    }
}

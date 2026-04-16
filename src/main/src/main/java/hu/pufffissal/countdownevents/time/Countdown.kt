package hu.pufffissal.countdownevents.time

import kotlin.math.absoluteValue

data class CountdownParts(
    val isCompleted: Boolean,
    val totalSecondsAbs: Long,
    val daysAbs: Long,
    val hoursAbs: Long,
    val minutesAbs: Long,
    val secondsAbs: Long,
)

fun countdownParts(nowEpochMillis: Long, targetEpochMillis: Long): CountdownParts {
    val diffSeconds = ((targetEpochMillis - nowEpochMillis) / 1000L)
    val isCompleted = diffSeconds <= 0L
    val abs = diffSeconds.absoluteValue
    val days = abs / 86400L
    val hours = (abs % 86400L) / 3600L
    val minutes = (abs % 3600L) / 60L
    val seconds = abs % 60L
    return CountdownParts(
        isCompleted = isCompleted,
        totalSecondsAbs = abs,
        daysAbs = days,
        hoursAbs = hours,
        minutesAbs = minutes,
        secondsAbs = seconds
    )
}


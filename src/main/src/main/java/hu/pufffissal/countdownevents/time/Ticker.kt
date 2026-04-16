package hu.pufffissal.countdownevents.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Emits current epoch millis aligned to the next second boundary.
 * This keeps the countdown stable across midnight / DST transitions because it always uses epoch time.
 */
fun secondTicker(): Flow<Long> = flow {
    while (true) {
        val now = System.currentTimeMillis()
        emit(now)
        val next = ((now / 1000L) + 1L) * 1000L
        delay((next - now).coerceAtLeast(50L))
    }
}


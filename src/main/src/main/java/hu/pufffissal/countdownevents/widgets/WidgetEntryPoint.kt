package hu.pufffissal.countdownevents.widgets

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.pufffissal.countdownevents.data.EventRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun repo(): EventRepository
    fun prefs(): WidgetPrefs
}


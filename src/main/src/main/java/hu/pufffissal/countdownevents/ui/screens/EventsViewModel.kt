package hu.pufffissal.countdownevents.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.pufffissal.countdownevents.data.Event
import hu.pufffissal.countdownevents.data.EventRepository
import hu.pufffissal.countdownevents.data.SortMode
import hu.pufffissal.countdownevents.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventsUiState(
    val sortMode: SortMode = SortMode.BY_DEADLINE,
    val events: List<Event> = emptyList(),
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repo: EventRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    val ui: StateFlow<EventsUiState> =
        combine(repo.observeSortMode(), repo.observeEvents()) { sort, events ->
            EventsUiState(sortMode = sort, events = events)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EventsUiState())

    fun setSortMode(mode: SortMode) {
        repo.setSortMode(mode)
    }

    fun toggleSortMode() {
        repo.toggleSortMode()
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            reminderScheduler.cancel(id)
            repo.delete(id)
        }
    }
}


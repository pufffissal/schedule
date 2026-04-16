package hu.pufffissal.countdownevents.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.pufffissal.countdownevents.data.prefs.UserPreferences
import hu.pufffissal.countdownevents.notifications.ReminderScheduler
import hu.pufffissal.countdownevents.widgets.WidgetRefresher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val widgetRefresher: WidgetRefresher,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    val hapticsEnabled: StateFlow<Boolean> = userPreferences.hapticsEnabled

    fun canScheduleExactAlarms(): Boolean = reminderScheduler.canScheduleExact()

    fun setHapticsEnabled(enabled: Boolean) {
        userPreferences.setHapticsEnabled(enabled)
    }

    fun refreshWidgetsNow() {
        viewModelScope.launch {
            widgetRefresher.refreshAll()
        }
    }
}

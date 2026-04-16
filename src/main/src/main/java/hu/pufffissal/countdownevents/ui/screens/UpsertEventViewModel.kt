package hu.pufffissal.countdownevents.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.pufffissal.countdownevents.data.EventRepository
import hu.pufffissal.countdownevents.notifications.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UpsertEventState(
    val isEdit: Boolean = false,
    val name: String = "",
    val nameError: String? = null,
    val targetEpochMillis: Long? = null,
    val targetMissing: Boolean = false,
    val shakeNonce: Int = 0,
    val iconKey: String? = null,
    val accentArgb: Int? = null,
    val reminderEpochMillis: Long? = null,
    val saving: Boolean = false,
    val canDelete: Boolean = false,
)

@HiltViewModel
class UpsertEventViewModel @Inject constructor(
    private val repo: EventRepository,
    private val scheduler: ReminderScheduler,
) : ViewModel() {

    private val _state = MutableStateFlow(UpsertEventState())
    val state: StateFlow<UpsertEventState> = _state

    private var editingId: Long? = null

    fun loadIfEdit(eventId: Long?) {
        if (eventId == null || eventId == 0L) return
        if (editingId == eventId) return
        editingId = eventId
        viewModelScope.launch {
            val e = repo.getEvent(eventId) ?: return@launch
            _state.value = UpsertEventState(
                isEdit = true,
                name = e.name,
                targetEpochMillis = e.targetEpochMillis,
                targetMissing = false,
                iconKey = e.iconKey,
                accentArgb = e.accentArgb,
                reminderEpochMillis = e.reminderEpochMillis,
                canDelete = true,
            )
        }
    }

    fun setName(v: String) {
        _state.update { it.copy(name = v, nameError = null) }
    }

    fun setTargetEpochMillis(v: Long) {
        _state.update { it.copy(targetEpochMillis = v, targetMissing = false) }
    }

    fun setIconKey(v: String?) {
        _state.update { it.copy(iconKey = v) }
    }

    fun setAccent(argb: Int?) {
        _state.update { it.copy(accentArgb = argb) }
    }

    fun setReminderEpochMillis(v: Long?) {
        _state.update { it.copy(reminderEpochMillis = v) }
    }

    fun save(onSuccess: () -> Unit, onInvalid: () -> Unit) {
        val s = _state.value
        val name = s.name.trim()
        val target = s.targetEpochMillis
        val missingName = name.isEmpty()
        val missingTarget = target == null
        if (missingName || missingTarget) {
            _state.update {
                it.copy(
                    nameError = if (missingName) "Add meg az esemény nevét." else null,
                    targetMissing = missingTarget,
                    shakeNonce = it.shakeNonce + 1,
                )
            }
            onInvalid()
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(saving = true) }
            val id = repo.upsert(
                id = editingId,
                name = name,
                targetEpochMillis = target!!,
                iconKey = s.iconKey,
                accentArgb = s.accentArgb,
                reminderEpochMillis = s.reminderEpochMillis
            )
            editingId = id
            val reminder = s.reminderEpochMillis
            if (reminder != null && reminder > System.currentTimeMillis()) {
                scheduler.schedule(id, reminder)
            } else {
                scheduler.cancel(id)
            }
            _state.update { it.copy(saving = false, isEdit = true, canDelete = true) }
            onSuccess()
        }
    }

    fun delete(onDone: () -> Unit) {
        val id = editingId ?: return
        viewModelScope.launch {
            scheduler.cancel(id)
            repo.delete(id)
            onDone()
        }
    }
}

package hu.pufffissal.countdownevents.data

data class Event(
    val id: Long,
    val name: String,
    val targetEpochMillis: Long,
    val createdAtEpochMillis: Long,
    val iconKey: String?,
    val accentArgb: Int?,
    val reminderEpochMillis: Long?,
)


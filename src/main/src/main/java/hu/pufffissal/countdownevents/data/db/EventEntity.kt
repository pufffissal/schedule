package hu.pufffissal.countdownevents.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
    indices = [
        Index(value = ["targetEpochMillis"]),
        Index(value = ["createdAtEpochMillis"])
    ]
)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    /**
     * The target instant in UTC epoch millis.
     * We store as millis to be time-zone aware; display/countdown uses current device ZoneId.
     */
    val targetEpochMillis: Long,
    val createdAtEpochMillis: Long,
    /**
     * Material icon id we map to a vector resource in-app.
     */
    val iconKey: String?,
    /**
     * ARGB color int (e.g. 0xFFFF0000). Null => no accent.
     */
    val accentArgb: Int?,
    /**
     * Reminder epoch millis (UTC). Null => no reminder.
     */
    val reminderEpochMillis: Long?,
)


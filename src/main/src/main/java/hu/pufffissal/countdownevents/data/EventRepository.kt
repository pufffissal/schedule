package hu.pufffissal.countdownevents.data

import hu.pufffissal.countdownevents.data.db.EventDao
import hu.pufffissal.countdownevents.data.db.EventEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Singleton
class EventRepository @Inject constructor(
    private val dao: EventDao,
) {
    private val sortMode = MutableStateFlow(SortMode.BY_DEADLINE)

    fun observeSortMode(): Flow<SortMode> = sortMode

    fun setSortMode(mode: SortMode) {
        sortMode.value = mode
    }

    fun toggleSortMode() {
        sortMode.update { prev ->
            when (prev) {
                SortMode.BY_DEADLINE -> SortMode.BY_CREATED
                SortMode.BY_CREATED -> SortMode.BY_DEADLINE
            }
        }
    }

    fun observeEvents(): Flow<List<Event>> {
        val byDeadline = dao.observeByTargetAsc()
        val byCreated = dao.observeByCreatedDesc()
        return sortMode.flatMapLatest { mode ->
            when (mode) {
                SortMode.BY_DEADLINE -> byDeadline
                SortMode.BY_CREATED -> byCreated
            }
        }.map { list -> list.map { it.toDomain() } }
    }

    fun observeEvent(id: Long): Flow<Event?> =
        dao.observeById(id).map { it?.toDomain() }

    suspend fun getEvent(id: Long): Event? = dao.getById(id)?.toDomain()

    suspend fun upsert(
        id: Long?,
        name: String,
        targetEpochMillis: Long,
        iconKey: String?,
        accentArgb: Int?,
        reminderEpochMillis: Long?,
    ): Long {
        val now = System.currentTimeMillis()
        val entity = if (id == null || id == 0L) {
            EventEntity(
                id = 0L,
                name = name,
                targetEpochMillis = targetEpochMillis,
                createdAtEpochMillis = now,
                iconKey = iconKey,
                accentArgb = accentArgb,
                reminderEpochMillis = reminderEpochMillis,
            )
        } else {
            val existing = dao.getById(id)
            EventEntity(
                id = id,
                name = name,
                targetEpochMillis = targetEpochMillis,
                createdAtEpochMillis = existing?.createdAtEpochMillis ?: now,
                iconKey = iconKey,
                accentArgb = accentArgb,
                reminderEpochMillis = reminderEpochMillis,
            )
        }
        return dao.upsert(entity)
    }

    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    suspend fun getNextEvents(limit: Int): List<Event> =
        dao.getNextByTarget(limit).map { it.toDomain() }
}

private fun EventEntity.toDomain(): Event =
    Event(
        id = id,
        name = name,
        targetEpochMillis = targetEpochMillis,
        createdAtEpochMillis = createdAtEpochMillis,
        iconKey = iconKey,
        accentArgb = accentArgb,
        reminderEpochMillis = reminderEpochMillis,
    )


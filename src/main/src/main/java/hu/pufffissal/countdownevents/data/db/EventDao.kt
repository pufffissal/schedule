package hu.pufffissal.countdownevents.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY targetEpochMillis ASC")
    fun observeByTargetAsc(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY createdAtEpochMillis DESC")
    fun observeByCreatedDesc(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<EventEntity?>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: EventEntity): Long

    @Update
    suspend fun update(entity: EventEntity)

    @Delete
    suspend fun delete(entity: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM events ORDER BY targetEpochMillis ASC LIMIT :limit")
    suspend fun getNextByTarget(limit: Int): List<EventEntity>
}


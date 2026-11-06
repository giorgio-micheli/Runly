package io.github.giorgiomicheli.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import io.github.giorgiomicheli.core.database.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    // If we try to insert a run where its id already exist, that record will be updated with the new run data
    @Upsert
    suspend fun upsertRun(run: RunEntity)

    @Upsert
    suspend fun upsertRuns(runs: List<RunEntity>)

    @Query("SELECT * FROM runs ORDER BY date_time_utc DESC")
    fun getRuns(): Flow<List<RunEntity>>

    @Query("DELETE FROM runs WHERE id = :id")
    suspend fun deleteRun(id: String)

    @Query("DELETE FROM runs")
    suspend fun deleteAllRuns()
}
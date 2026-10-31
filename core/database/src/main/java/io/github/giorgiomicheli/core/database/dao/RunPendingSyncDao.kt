package io.github.giorgiomicheli.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import io.github.giorgiomicheli.core.database.entity.DeletedRunSyncEntity
import io.github.giorgiomicheli.core.database.entity.RunPendingSyncEntity
import io.github.giorgiomicheli.core.domain.run.RunId

@Dao
interface RunPendingSyncDao {

    @Query("SELECT * FROM run_pending_sync_entities WHERE user_id = :userId")
    suspend fun getAllRunPendingSyncEntities(userId: String): List<RunPendingSyncEntity>

    @Query("SELECT * FROM run_pending_sync_entities WHERE run_id = :runId")
    suspend fun getRunPendingSyncEntity(runId: String): RunPendingSyncEntity?

    @Upsert
    suspend fun upsertRunPendingSyncEntity(entity: RunPendingSyncEntity)

    @Delete
    suspend fun deleteRunPendingSyncEntity(runId: String)

    @Query("SELECT * FROM deleted_run_sync_entities WHERE user_id = :userId")
    suspend fun getAllDeleteRunSyncEntities(userId: String): List<DeletedRunSyncEntity>

    @Upsert
    suspend fun upsertDeleteRunSyncEntity(entity: DeletedRunSyncEntity)

    @Query("DELETE FROM deleted_run_sync_entities WHERE run_id = :runId")
    suspend fun deleteDeletedRunSyncEntity(runId: String)
}
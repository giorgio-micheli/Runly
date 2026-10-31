package io.github.giorgiomicheli.core.domain.run

import io.github.giorgiomicheli.core.domain.util.DataFailure
import io.github.giorgiomicheli.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface RunRepository {

    fun getRuns(): Flow<List<Run>>
    suspend fun fetchRuns(): EmptyResult<DataFailure>
    suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataFailure>
    suspend fun deleteRun(id: RunId)

    suspend fun syncPendingRuns()
}
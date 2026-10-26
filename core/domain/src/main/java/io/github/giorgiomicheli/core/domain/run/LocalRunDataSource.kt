package io.github.giorgiomicheli.core.domain.run
import io.github.giorgiomicheli.core.domain.util.DataFailure
import io.github.giorgiomicheli.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

typealias RunId = String

/*
    In other projects I called this a "repository" which duty is to abstract the operations
    that we can do on a specific datasource, without even knowing about which specific datasource
    we're using.
 */
interface LocalRunDataSource {

    fun getRuns(): Flow<List<Run>>
    suspend fun upsertRun(run: Run): Result<RunId, DataFailure.Local>
    suspend fun upsertRuns(runList: List<Run>): Result<List<RunId>, DataFailure.Local>
    suspend fun deleteRun(id: String)
    suspend fun deleteAllRuns()




}
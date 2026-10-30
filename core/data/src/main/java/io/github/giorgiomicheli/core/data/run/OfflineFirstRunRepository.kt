package io.github.giorgiomicheli.core.data.run

import io.github.giorgiomicheli.core.domain.run.LocalRunDataSource
import io.github.giorgiomicheli.core.domain.run.RemoteRunDataSource
import io.github.giorgiomicheli.core.domain.run.Run
import io.github.giorgiomicheli.core.domain.run.RunId
import io.github.giorgiomicheli.core.domain.run.RunRepository
import io.github.giorgiomicheli.core.domain.util.DataFailure
import io.github.giorgiomicheli.core.domain.util.EmptyResult
import io.github.giorgiomicheli.core.domain.util.Result
import io.github.giorgiomicheli.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope
) : RunRepository {

    override fun getRuns(): Flow<List<Run>> {
        return localRunDataSource.getRuns()
    }

    override suspend fun fetchRuns(): EmptyResult<DataFailure> {
        return when(val result = remoteRunDataSource.getRuns()) {
            is Result.Failure -> result.asEmptyDataResult()
            is Result.Success -> {
                /*
                 We launch a new indipendent coroutine in the application scope.
                 The fetchRuns() function is executed in another scope, usually the viewModelScope,
                 that's why we execute this coroutine in the application scope, otherwise if the
                 user navigates back from the screen, the associated viewModel could be deleted
                 and all of its related coroutine cancelled. If we execute the upserRuns in a coroutine
                 and it gets cancelled, then we could potentially be in an inconsistent state.
                 */
                applicationScope.async {
                    localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataFailure> {
        // We insert the newly created run in the local database
        val localResult = localRunDataSource.upsertRun(run)
        // If the disk is full we return
        if (localResult !is Result.Success) return localResult.asEmptyDataResult()

        // We synchronize the local data to the back-end by sending a post request with the Run
        // object with the ID
        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPicture
        )

        return when (remoteResult) {
            is Result.Failure -> {
                //TODO: will handle this case later
                Result.Success(Unit)
            }
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()
    }
}
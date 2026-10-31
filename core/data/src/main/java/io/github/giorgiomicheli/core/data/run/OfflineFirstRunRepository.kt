package io.github.giorgiomicheli.core.data.run

import io.github.giorgiomicheli.core.database.dao.RunPendingSyncDao
import io.github.giorgiomicheli.core.database.mappers.toDomain
import io.github.giorgiomicheli.core.domain.SessionStorage
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage
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
        /*
        EDGE CASE:
        If the run that we're deleting hasn't been synced to the backend yet,
        so there's still a record related to it in the pending runs table,
        we just delete that record.
        Since the creation and the deletion of this run only happend locally
        we don't have to send anything to the backend, so we return.
         */
        val hasAPendingSync = runPendingSyncDao.getRunPendingSyncEntity(id) != null
        if (hasAPendingSync) {
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userID ?: return@withContext

            // The two next calls are independent of each other, so we can use async to create
            // two new coroutines and execute them in parallel.
            val pendingRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }
            val pendingDeletions = async {
                runPendingSyncDao.getAllDeleteRunSyncEntities(userId)
            }

            // this variable will be a reference to a list of jobs.
            val pendingRunsJob = pendingRuns
                .await() // blocks until it has the result
                .map {
                    // We create a new coroutine for every pending run, they can be executed in parallel
                    launch {
                        val run = it.run.toDomain()
                        when(remoteRunDataSource.postRun(run, it.mapPictureBytes)) {
                            // If the api call fails, we don't do anything. We'll retry the next time we call 'syncPendingRuns'
                            is Result.Failure -> Unit
                            is Result.Success -> {
                                // The pending run was successfully synced with the backend and saved in the remote database.
                                // We can now safely delete it from our local database.
                                // If this coroutine is cancelled after making the api call, the next coroutine would still be executed
                                // cause it's attached to the applicationScope and not to the viewModel's one.
                                applicationScope.launch {
                                    runPendingSyncDao.deleteRunPendingSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            val pendingDeletionsJob = pendingDeletions
                .await() // blocks until it has the result
                .map {
                    // We create a new coroutine for every pending run, they can be executed in parallel
                    launch {
                        when(remoteRunDataSource.deleteRun(it.runId)) {
                            // If the api call fails, we don't do anything. We'll retry the next time we call 'syncPendingRuns'
                            is Result.Failure -> Unit
                            is Result.Success -> {
                                // The pending run was successfully synced with the backend and saved in the remote database.
                                // We can now safely delete it from our local database.
                                // If this coroutine is cancelled after making the api call, the next coroutine would still be executed
                                // cause it's attached to the applicationScope and not to the viewModel's one.
                                applicationScope.launch {
                                    runPendingSyncDao.deleteDeletedRunSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            // We wait for all jobs to finish, we're suspending not blocking. In the meanwhile the thread that is executing
            // this whole coroutine can do something else.
            pendingRunsJob.joinAll()
            pendingDeletionsJob.joinAll()
        }
    }
}
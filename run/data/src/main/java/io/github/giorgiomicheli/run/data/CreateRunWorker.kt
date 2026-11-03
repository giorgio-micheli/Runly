package io.github.giorgiomicheli.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.giorgiomicheli.core.database.dao.RunPendingSyncDao
import io.github.giorgiomicheli.core.database.mappers.toDomain
import io.github.giorgiomicheli.core.domain.run.RemoteRunDataSource

/*
    If we try to create a run remotely, but the call fails, we need to retry later
 */
class CreateRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingSyncDao: RunPendingSyncDao
): CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        // From the fifth retry we just return a failure, cause something seems to be off.
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        // We take the runId passed in the params bundle and we retrieve the actual entity from the
        // local db. Cause the run wasn't created remotely due to a failure but it was inserted in
        // our local db, so there we have the record with all of its data.
        val pendingRunId = params.inputData.getString(RUN_ID) ?: return Result.failure()
        val pendingRunEntity = pendingSyncDao.getRunPendingSyncEntity(pendingRunId)
            ?: return Result.failure()

        val run = pendingRunEntity.run.toDomain()
        return when(val result = remoteRunDataSource.postRun(run, pendingRunEntity.mapPictureBytes)) {
            is io.github.giorgiomicheli.core.domain.util.Result.Failure -> {
                result.failure.toWorkerResult()
            }
            is io.github.giorgiomicheli.core.domain.util.Result.Success -> {
                pendingSyncDao.deleteRunPendingSyncEntity(pendingRunId)
                Result.success()
            }
        }


    }

    companion object {
        const val RUN_ID = "RUN_ID"
    }


}
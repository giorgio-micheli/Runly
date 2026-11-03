package io.github.giorgiomicheli.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.giorgiomicheli.core.database.dao.RunPendingSyncDao
import io.github.giorgiomicheli.core.domain.run.RemoteRunDataSource

class DeleteRunWorker(
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
        // inputData is like a Bundle
        val runId = params.inputData.getString(RUN_ID) ?: return Result.failure()
        // we try to delete the remote run
        return when(val result = remoteRunDataSource.deleteRun(runId)) {
            is io.github.giorgiomicheli.core.domain.util.Result.Failure -> {
                // if there's an error, we handle it
                result.failure.toWorkerResult()
            }
            is io.github.giorgiomicheli.core.domain.util.Result.Success -> {
                // if the remote run was deleted correctly we delete its record in the deleted_run_entities table
                // in the local db
                pendingSyncDao.deleteDeletedRunSyncEntity(runId)
                Result.success()
            }
        }
    }

    companion object {
        const val RUN_ID = "RUN_ID"
    }
}
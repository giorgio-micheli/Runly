package io.github.giorgiomicheli.run.data

import android.content.Context
import android.content.ContextParams
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.giorgiomicheli.core.domain.run.RunRepository
import io.github.giorgiomicheli.core.domain.util.DataFailure

class FetchRunsWorker(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository
): CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        // From the fifth retry we just return a failure, cause something seems to be off.
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        return when(val result = runRepository.fetchRuns()) {
            is io.github.giorgiomicheli.core.domain.util.Result.Failure -> {
                // Depending on the type of the error, we decide if we want to retry the worker
                // or consider the error too bad to even retry, cause it'll likely fail again.
                result.failure.toWorkerResult()
            }
            is io.github.giorgiomicheli.core.domain.util.Result.Success -> {
                Result.success()
            }
        }
    }
}
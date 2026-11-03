package io.github.giorgiomicheli.run.data

import androidx.work.ListenableWorker
import io.github.giorgiomicheli.core.domain.util.DataFailure

fun DataFailure.toWorkerResult(): ListenableWorker.Result {
    return when(this) {
        DataFailure.Local.DISK_FULL -> ListenableWorker.Result.failure()
        DataFailure.Network.REQUEST_TIMEOUT -> ListenableWorker.Result.retry()
        DataFailure.Network.UNAUTHORIZED -> ListenableWorker.Result.retry()
        DataFailure.Network.CONFLICT -> ListenableWorker.Result.retry()
        DataFailure.Network.TOO_MANY_REQUESTS -> ListenableWorker.Result.retry()
        DataFailure.Network.NO_INTERNET -> ListenableWorker.Result.retry()
        DataFailure.Network.PAYLOAD_TOO_LARGE -> ListenableWorker.Result.failure()
        DataFailure.Network.SERVER_ERROR -> ListenableWorker.Result.retry()
        DataFailure.Network.SERIALIZATION -> ListenableWorker.Result.failure()
        DataFailure.Network.UNKNOWN -> ListenableWorker.Result.failure()
    }
}
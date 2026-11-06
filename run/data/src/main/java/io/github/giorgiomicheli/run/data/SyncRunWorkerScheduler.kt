package io.github.giorgiomicheli.run.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import io.github.giorgiomicheli.core.database.dao.RunPendingSyncDao
import io.github.giorgiomicheli.core.database.entity.DeletedRunSyncEntity
import io.github.giorgiomicheli.core.database.entity.RunPendingSyncEntity
import io.github.giorgiomicheli.core.database.mappers.toEntity
import io.github.giorgiomicheli.core.domain.SessionStorage
import io.github.giorgiomicheli.core.domain.run.Run
import io.github.giorgiomicheli.core.domain.run.RunId
import io.github.giorgiomicheli.core.domain.run.SyncRunScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunWorkerScheduler(
    private val context: Context,
    private val pendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
): SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleSync(type: SyncRunScheduler.SyncType) {
        when(type) {
            is SyncRunScheduler.SyncType.CreateRun -> scheduleCreateRunWorker(type.run, type.mapPictureBytes)
            is SyncRunScheduler.SyncType.DeleteRun -> scheduleDeleteRunWorker(type.runId)
            is SyncRunScheduler.SyncType.FetchRuns -> scheduleFetchRunsWorker(type.interval)
        }
    }

    private suspend fun scheduleDeleteRunWorker(runID: RunId) {
        val userID = sessionStorage.get()?.userID ?: return
        val entity = DeletedRunSyncEntity(
            runId = runID,
            userId = userID
        )
        pendingSyncDao.upsertDeleteRunSyncEntity(entity)

        val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .addTag("delete_work")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(DeleteRunWorker.RUN_ID, entity.runId)
                    .build()
            )
            .build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    private suspend fun scheduleCreateRunWorker(run: Run,  mapPictureBytes: ByteArray) {
        val userID = sessionStorage.get()?.userID ?: return
        val pendingRun = RunPendingSyncEntity(
            run = run.toEntity(),
            mapPictureBytes = mapPictureBytes,
            userId = userID
        )
        pendingSyncDao.upsertRunPendingSyncEntity(pendingRun)

        val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .addTag("create_work")
            .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            ).setBackoffCriteria(
            backoffPolicy = BackoffPolicy.EXPONENTIAL,
            backoffDelay = 2000L,
            timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(CreateRunWorker.RUN_ID, pendingRun.runId)
                    .build()
            )
            .build()

            applicationScope.launch {
                workManager.enqueue(workRequest).await()
            }.join()
    }

    private suspend fun scheduleFetchRunsWorker(interval: Duration) {
        val isSyncScheduled = withContext(Dispatchers.IO) {
            workManager.getWorkInfosByTag("sync_work")
                .get()
                .isNotEmpty()
        }
        if (isSyncScheduled) return

        /*
        A constraint is just a condition that has to be met in order for WorkManager to execute this
        work request.

        The backoff criteria sets the interval to retry the work request if for example a server
        side error happens. If there is a server side issue that won't probably go away after only
        two seconds. That's why we chose an exponential backoff.

        The initial delay indicates that we wait 30 minutes to let WorkManager execute the first
        work request of this type. That's because we schedule this worker in the init block of the
        RunOverview viewModel, but in that init block we already fetch the runs remotely. Our local
        database is already up to date at that point if the api call doesn't fail. The next sync
        will only be necessary after about 15/30 minutes (we chose 30).

        The tag is a way to identify this specific type of work request and check if there's already
        a work request of this type running.

         */
        val workRequest = PeriodicWorkRequestBuilder<FetchRunsWorker>(
            repeatInterval = interval.toJavaDuration()
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
        ).setInitialDelay(
            duration = 30,
            timeUnit = TimeUnit.MINUTES
        )
            .addTag("sync_work")
            .build()

        workManager.enqueue(workRequest).await()
    }

    override suspend fun cancelAllSyncs() {
        workManager
            .cancelAllWork()
            .await()
    }
}
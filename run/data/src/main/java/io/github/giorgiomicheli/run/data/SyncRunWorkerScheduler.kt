package io.github.giorgiomicheli.run.data

import android.content.Context
import androidx.work.WorkManager
import androidx.work.await
import io.github.giorgiomicheli.core.database.dao.RunPendingSyncDao
import io.github.giorgiomicheli.core.domain.SessionStorage
import io.github.giorgiomicheli.run.domain.SyncRunScheduler
import kotlin.time.Duration

class SyncRunWorkerScheduler(
    private val context: Context,
    private val pendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage
): SyncRunScheduler {


    override suspend fun scheduleSync(type: SyncRunScheduler.SyncType) {
        TODO("Not yet implemented")
    }

    private suspend fun scheduleFetchRunsWorker(interval: Duration) {

    }

    override suspend fun cancelAllSyncs() {
        WorkManager.getInstance(context)
            .cancelAllWork()
            .await()
    }
}
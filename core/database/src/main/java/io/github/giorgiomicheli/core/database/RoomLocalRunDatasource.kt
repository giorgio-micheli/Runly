package io.github.giorgiomicheli.core.database

import android.database.sqlite.SQLiteFullException
import io.github.giorgiomicheli.core.database.dao.RunDao
import io.github.giorgiomicheli.core.database.mappers.toDomain
import io.github.giorgiomicheli.core.database.mappers.toEntity
import io.github.giorgiomicheli.core.domain.run.LocalRunDataSource
import io.github.giorgiomicheli.core.domain.run.Run
import io.github.giorgiomicheli.core.domain.run.RunId
import io.github.giorgiomicheli.core.domain.util.DataFailure
import io.github.giorgiomicheli.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalRunDatasource(
    private val runDao: RunDao
) : LocalRunDataSource {

    override fun getRuns(): Flow<List<Run>> {
        return runDao.getRuns()
            // Flow Map intermediate operator
            .map { listOfEntities ->
                // Collection Map intermediate operator
                listOfEntities.map {entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun upsertRun(run: Run): Result<RunId, DataFailure.Local> = try {
            val entity = run.toEntity()
            runDao.upsertRun(entity)
            Result.Success(entity.id)
        } catch (_: SQLiteFullException) {
            Result.Failure(DataFailure.Local.DISK_FULL)
        }

    override suspend fun upsertRuns(runList: List<Run>): Result<List<RunId>, DataFailure.Local> = try {
        val entities = runList.map { it.toEntity() }
        runDao.upsertRuns(entities)
        Result.Success(entities.map { it.id })
    } catch (_: SQLiteFullException) {
        Result.Failure(DataFailure.Local.DISK_FULL)
    }

    override suspend fun deleteRun(id: String) {
        runDao.deleteRun(id)
    }

    override suspend fun deleteAllRuns() {
        runDao.deleteAllRuns()
    }
}
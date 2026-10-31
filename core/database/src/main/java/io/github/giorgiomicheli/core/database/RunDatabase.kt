package io.github.giorgiomicheli.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.giorgiomicheli.core.database.dao.RunDao
import io.github.giorgiomicheli.core.database.dao.RunPendingSyncDao
import io.github.giorgiomicheli.core.database.entity.DeletedRunSyncEntity
import io.github.giorgiomicheli.core.database.entity.RunEntity
import io.github.giorgiomicheli.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunSyncEntity::class
    ],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao




}
package io.github.giorgiomicheli.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.giorgiomicheli.core.database.dao.RunDao
import io.github.giorgiomicheli.core.database.entity.RunEntity

@Database(
    entities = [RunEntity::class],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao




}
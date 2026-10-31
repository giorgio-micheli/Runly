package io.github.giorgiomicheli.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*

   Runs that were deleted locally but the remote deletion failed. They still need to be synced
   with the back-end.

   To delete a run we only need its ID and its user's ID.
 */
@Entity(
    tableName = "deleted_run_sync_entities"
)
data class DeletedRunSyncEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "run_id")
    val runId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
)

package io.github.giorgiomicheli.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.giorgiomicheli.core.domain.run.RunId

/*
    This table will be used to save the runs that were created but couldn't be synced to the backend.

    We need to also save the byte array representing the map's screenshot.
    Cause it's as well not being sent to the back-end yet.

    We also want to associate a PendingSyncEntity with a specific user, this way every pending run
    gets synced to the backend with the correct user.
 */
@Entity(
    tableName = "run_pending_sync_entities"
)
data class RunPendingSyncEntity(
    @Embedded
    val run: RunEntity,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "run_id")
    val runId: String = run.id,
    @ColumnInfo(name = "map_picture_bytes")
    val mapPictureBytes: ByteArray,
    @ColumnInfo(name = "user_id")
    val userId: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RunPendingSyncEntity

        if (run != other.run) return false
        if (runId != other.runId) return false
        if (!mapPictureBytes.contentEquals(other.mapPictureBytes)) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = run.hashCode()
        result = 31 * result + runId.hashCode()
        result = 31 * result + mapPictureBytes.contentHashCode()
        result = 31 * result + userId.hashCode()
        return result
    }
}
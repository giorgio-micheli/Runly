package io.github.giorgiomicheli.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bson.types.ObjectId

@Entity(
    tableName = "runs"
)
data class RunEntity(
    /*
    The back-end uses MongoDB as a database, and MongoDB uses the concept of ObjectIds for
    representing an ID and so a primary key. With the library BSON we create this object ids.
    This way the ids will be the same on our local database and also on the back-end's database.
     */
    @PrimaryKey(autoGenerate = false)
    val id: String = ObjectId().toHexString(),
    @ColumnInfo(name = "duration_millis")
    val durationMillis: Long,
    @ColumnInfo(name = "distance_meters")
    val distanceMeters: Int,
    @ColumnInfo(name = "date_time_utc")
    val dateTimeUtc: String,
    val latitude: Double,
    val longitude: Double,
    @ColumnInfo(name = "avg_speed_kmh")
    val avgSpeedKmh: Double,
    @ColumnInfo(name = "max_speed_kmh")
    val maxSpeedKmh: Double,
    @ColumnInfo(name = "total_elevation_meters")
    val totalElevationMeters: Int,
    @ColumnInfo(name = "map_picture_url")
    val mapPictureUrl: String?
)

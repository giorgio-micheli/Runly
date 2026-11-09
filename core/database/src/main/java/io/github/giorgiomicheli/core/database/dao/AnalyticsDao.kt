package io.github.giorgiomicheli.core.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AnalyticsDao {
    @Query("SELECT SUM(distance_meters) FROM runs")
    suspend fun getTotalDistance(): Int

    @Query("SELECT SUM(duration_millis) FROM runs")
    suspend fun getTotalTimeRun(): Long

    @Query("SELECT MAX(max_speed_kmh) FROM runs")
    suspend fun getMaxRunSpeed(): Double

    @Query("SELECT AVG(distance_meters) FROM runs")
    suspend fun getAvgDistancePerRun(): Double

    // we convert in minutes and km
    @Query("SELECT AVG((duration_millis / 60000.0) / (distance_meters / 1000.0)) FROM runs")
    suspend fun getAvgPacePerRun(): Double
}
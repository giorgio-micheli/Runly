package io.github.giorgiomicheli.analytics.presentation

import io.github.giorgiomicheli.analytics.domain.AnalyticsValues
import io.github.giorgiomicheli.core.presentation.ui.formatted
import io.github.giorgiomicheli.core.presentation.ui.toFormattedKM
import io.github.giorgiomicheli.core.presentation.ui.toFormattedKmh
import io.github.giorgiomicheli.core.presentation.ui.toFormattedMeters
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

fun Duration.toFormattedTotalTime(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60

    return "${days} d ${hours}h ${minutes}m"
}

fun AnalyticsValues.toAnalyticsDashboardState(): AnalyticsDashboardState {
    return AnalyticsDashboardState(
        totalDistanceRun = (totalDistanceRun / 1000.0).toFormattedKM(),
        totalTimeRun = totalTimeRun.toFormattedTotalTime(),
        fastestEverRun = fastestEverRun.toFormattedKmh(),
        avgDistance = (avgDistancePerRun / 1000.0).toFormattedKM(),
        avgPace = avgPacePerRun.seconds.formatted()
    )
}
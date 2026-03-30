package org.mongo.mqs.model

import kotlinx.serialization.Serializable

@Serializable
data class MetricStats(
    val org: String,
    val project: String,
    val cluster: String,
    val instance: String,
    val avgCpu: Double,
//    val maxCpu: Double,
    val avgDisk: Double,
//    val maxDisk: Double,
    val inserts: Double
)

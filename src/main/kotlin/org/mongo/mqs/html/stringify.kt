package org.mongo.mqs.html

import org.bson.Document
import org.bson.json.JsonWriterSettings
import org.mongo.mqs.model.MetricStats
import org.mongo.mqs.model.QueryShape
import org.mongo.mqs.model.QueryStat
import kotlin.math.min

private val defaultJws = JsonWriterSettings.builder()
    .indent(true)
    .build()

fun QueryShape.prettyQuery(): String {
    return pipeline?.toJson()
        ?: filter?.toJson(defaultJws)
        ?: query?.toJson(defaultJws)
        ?: "Not Found!"
}

val QueryStat.shortHash
    get() = queryShapeHash.substring(0, min(queryShapeHash.lastIndex, 10)) + "..."

fun MetricStats.avgMs(queryStat: QueryStat) = avgMs(queryStat.metrics.execCount)

fun MetricStats.avgMs(executionCount: Long) = "${(sum / executionCount) / 1000} ms"

val MetricStats.maxMs get() = "${max / 1000} ms"
val MetricStats.minMs get() = "${min / 1000} ms"

fun List<Document>.toJson(jws: JsonWriterSettings = defaultJws) =
    joinToString(separator = ",\n", prefix = "[ ", postfix = " ]") { it.toJson(jws) }
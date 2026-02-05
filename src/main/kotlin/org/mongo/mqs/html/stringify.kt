package org.mongo.mqs.html

import org.bson.Document
import org.bson.json.JsonWriterSettings
import org.mongo.mqs.model.MetricStats
import org.mongo.mqs.model.QueryShape
import org.mongo.mqs.model.QueryStat
import kotlin.math.min

private val defaultJws = JsonWriterSettings.builder()
    .indent(false)
    .build()

private val prettyJws = JsonWriterSettings.builder()
    .indent(true)
    .build()

fun QueryShape.query(pretty: Boolean = false): String {
    val jws = if (pretty) prettyJws else defaultJws
    return pipeline?.toJson(jws)
        ?: filter?.toJson(jws)
        ?: query?.toJson(jws)
        ?: "Not Found!"
}

val QueryStat.shortHash
    get() = queryShapeHash.substring(0, min(queryShapeHash.lastIndex, 10)) + "..."

fun MetricStats.avgMs(queryStat: QueryStat) = avgMs(queryStat.metrics.execCount)

fun MetricStats.avgMs(executionCount: Long) = (sum / executionCount) / 1000

val MetricStats.maxMs get() = max / 1000
val MetricStats.minMs get() = min / 1000

val Double.pretty get() = "%.2f".format(this)

fun List<Document>.toJson(jws: JsonWriterSettings): String {
    val separator = if (jws.isIndent) ",\n" else ", "
    return joinToString(separator, prefix = "[ ", postfix = " ]") { it.toJson(jws) }
}
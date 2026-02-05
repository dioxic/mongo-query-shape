package org.mongo.mqs.export

import org.mongo.mqs.html.avgMs
import org.mongo.mqs.html.maxMs
import org.mongo.mqs.html.minMs
import org.mongo.mqs.html.query
import org.mongo.mqs.model.QueryStat

fun List<QueryStat>.toCsv(): String {
    val header = listOf(
        "hash",
        "namespace",
        "command",
        "query",
        "execCount",
        "avgMs",
        "maxMs",
        "minMs"
    ).joinToString(",")

    val rows = this.map { stat ->
        listOf(
            stat.queryShapeHash,
            "${stat.key.queryShape.cmdNs.db}.${stat.key.queryShape.cmdNs.coll}",
            stat.key.queryShape.command,
            stat.key.queryShape.query(),
            stat.metrics.execCount.toString(),
            stat.metrics.totalExecMicros.avgMs(stat),
            stat.metrics.totalExecMicros.maxMs,
            stat.metrics.totalExecMicros.minMs
        ).joinToString(",") { escapeCsv(it) }
    }

    return (listOf(header) + rows).joinToString("\n")
}

private fun escapeCsv(value: String): String {
    val needsQuotes = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")
    return if (needsQuotes) {
        "\"" + value.replace("\"", "\"\"") + "\""
    } else {
        value
    }
}

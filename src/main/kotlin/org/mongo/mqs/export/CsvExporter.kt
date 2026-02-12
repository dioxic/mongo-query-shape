package org.mongo.mqs.export

import org.mongo.mqs.html.avgMs
import org.mongo.mqs.html.maxMs
import org.mongo.mqs.html.minMs
import org.mongo.mqs.html.pretty
import org.mongo.mqs.html.query
import org.mongo.mqs.model.QueryShapeColumnVisibility
import org.mongo.mqs.model.QueryStat

fun List<QueryStat>.toCsv(
    columnVisibility: QueryShapeColumnVisibility = QueryShapeColumnVisibility.Default
): String {
    val header = buildList {
        add("hash")
        add("namespace")
        add("command")
        add("query")
        if (columnVisibility.execCount) add("execCount")
        if (columnVisibility.avgExec) add("avgMs")
        if (columnVisibility.maxExec) add("maxMs")
        if (columnVisibility.minExec) add("minMs")
        if (columnVisibility.targetScore) add("targetingScore")
    }.joinToString(",")

    val rows = this.map { stat ->
        buildList {
            add(stat.queryShapeHash)
            add("${stat.key.queryShape.cmdNs.db}.${stat.key.queryShape.cmdNs.coll}")
            add(stat.key.queryShape.command)
            add(stat.key.queryShape.query())
            if (columnVisibility.execCount) add(stat.metrics.execCount.toString())
            if (columnVisibility.avgExec) add(stat.metrics.totalExecMicros.avgMs(stat).toString())
            if (columnVisibility.maxExec) add(stat.metrics.totalExecMicros.maxMs.toString())
            if (columnVisibility.minExec) add(stat.metrics.totalExecMicros.minMs.toString())
            if (columnVisibility.targetScore) add(stat.metrics.targetingScore.pretty)
        }.joinToString(",") { escapeCsv(it) }
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

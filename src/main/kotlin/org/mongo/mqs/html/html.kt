package org.mongo.mqs.html

import kotlinx.html.TagConsumer
import kotlinx.html.*
import org.mongo.mqs.model.QueryStat

fun HTML.tableHtml(queryStats: List<QueryStat>) {
    head {
        title { +"Query Stats" }
        script { src = "https://unpkg.com/htmx.org@2.0.4" }
        style {
            unsafe {
                raw(
                    """
                                table { border-collapse: collapse; width: 100%; }
                                th, td { border: 1px solid black; padding: 8px; text-align: left; }
                                th { background-color: #f2f2f2; }
                                """.trimIndent()
                )
            }
        }
    }
    body {
        h1 { +"Query Statistics" }
        button {
            attributes["hx-get"] = "/stats"
            attributes["hx-target"] = "#stats-table"
            +"Refresh"
        }
        div {
            id = "stats-table"
            table {
                queryStatsTable(queryStats)
            }
        }
    }
}

fun TABLE.queryStatsTable(queryStats: List<QueryStat>) {
    thead {
        tr {
            th { +"queryShapeHash" }
            th { +"cmdNs" }
            th { +"execCount" }
            th { +"totalExecMicros.sum" }
            th { +"bytesRead.sum" }
        }
    }
    tbody {
        queryStats.forEach { stat ->
            tr {
                td { +stat.queryShapeHash }
                td { +"${stat.key.queryShape.cmdNs.db}.${stat.key.queryShape.cmdNs.coll}" }
                td { +stat.metrics.execCount.toString() }
                td { +stat.metrics.totalExecMicros.sum.toString() }
                td { +stat.metrics.bytesRead.sum.toString() }
            }
        }
    }
}
package org.mongo.mqs.html

import kotlinx.html.*
import org.bson.Document
import org.bson.json.JsonWriterSettings
import org.mongo.mqs.model.QueryShape
import org.mongo.mqs.model.QueryStat

fun HTML.tableHtml(queryStats: List<QueryStat>) {
    head {
        title { +"Query Stats" }
        script { src = "https://unpkg.com/htmx.org@2.0.4" }
        script { src = "https://cdn.tailwindcss.com" }
    }
    body {
        classes = setOf("bg-gray-100", "p-8", "font-sans")
        h1 {
            classes = setOf("text-3xl", "font-bold", "mb-6", "text-gray-800")
            +"Query Statistics"
        }
        button {
            classes = setOf(
                "mb-6", "px-4", "py-2", "bg-blue-600", "text-white",
                "rounded-lg", "hover:bg-blue-700", "transition-colors", "duration-200"
            )
            attributes["hx-get"] = "/stats"
            attributes["hx-target"] = "#stats-table"
            +"Refresh"
        }
        div {
            id = "stats-table"
            classes = setOf("overflow-x-auto", "bg-white", "rounded-lg", "shadow")
            table {
                classes = setOf("min-w-full", "divide-y", "divide-gray-200")
                queryStatsTable(queryStats)
            }
        }
    }
}

fun TABLE.queryStatsTable(queryStats: List<QueryStat>) {
    thead {
        classes = setOf("bg-gray-50")
        tr {
            val thClasses = setOf(
                "px-6", "py-3", "text-left", "text-xs", "font-medium",
                "text-gray-500", "uppercase", "tracking-wider"
            )
            th { classes = thClasses; +"Query Shape Hash" }
            th { classes = thClasses; +"Namespace" }
            th { classes = thClasses; +"Command" }
            th { classes = thClasses; +"Query" }
            th { classes = thClasses; +"Execution Count" }
            th { classes = thClasses; +"Total Exec Micros" }
            th { classes = thClasses; +"Bytes Read" }
        }
    }
    tbody {
        classes = setOf("bg-white", "divide-y", "divide-gray-200")
        queryStats.forEach { stat ->
            tr {
                val tdClasses = setOf("px-6", "py-4", "whitespace-nowrap", "text-sm", "text-gray-900")
                td { classes = tdClasses; +stat.queryShapeHash }
                td {
                    classes = tdClasses
                    +"${stat.key.queryShape.cmdNs.db}.${stat.key.queryShape.cmdNs.coll}"
                }
                td { classes = tdClasses; +stat.key.queryShape.command }
                td { classes = tdClasses; +stat.key.queryShape.prettyQuery() }
                td { classes = tdClasses; +stat.metrics.execCount.toString() }
                td { classes = tdClasses; +stat.metrics.totalExecMicros.sum.toString() }
                td { classes = tdClasses; +stat.metrics.bytesRead.sum.toString() }
            }
        }
    }
}
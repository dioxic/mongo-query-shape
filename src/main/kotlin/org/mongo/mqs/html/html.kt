package org.mongo.mqs.html

import kotlinx.html.*
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
        div {
            classes = setOf("flex", "space-x-4", "mb-6")
            button {
                classes = setOf(
                    "px-4", "py-2", "bg-blue-600", "text-white",
                    "rounded-lg", "hover:bg-blue-700", "transition-colors", "duration-200"
                )
                attributes["hx-get"] = "/stats"
                attributes["hx-target"] = "#stats-table"
                +"Refresh"
            }
            a {
                href = "/csv"
                classes = setOf(
                    "px-4", "py-2", "bg-green-600", "text-white",
                    "rounded-lg", "hover:bg-green-700", "transition-colors", "duration-200"
                )
                +"Download CSV"
            }
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
            th { classes = thClasses; +"Avg Execution" }
            th { classes = thClasses; +"Max Execution" }
            th { classes = thClasses; +"Min Execution" }
            th { classes = thClasses; +"Targeting Score" }
        }
    }
    tbody {
        classes = setOf("bg-white", "divide-y", "divide-gray-200")
        queryStats.forEach { stat ->
            tr {
                val tdClasses = setOf("px-6", "py-4", "whitespace-nowrap", "text-sm", "text-gray-900")
                td {
                    classes = tdClasses
                    title = stat.queryShapeHash
                    +stat.shortHash
                }
                td {
                    classes = tdClasses
                    +"${stat.key.queryShape.cmdNs.db}.${stat.key.queryShape.cmdNs.coll}"
                }
                td { classes = tdClasses; +stat.key.queryShape.command }
                td { classes = tdClasses; +stat.key.queryShape.query() }
                td { classes = tdClasses; +stat.metrics.execCount.toString() }
                td { classes = tdClasses; +"${stat.metrics.totalExecMicros.avgMs(stat)} ms" }
                td { classes = tdClasses; +"${stat.metrics.totalExecMicros.maxMs} ms" }
                td { classes = tdClasses; +"${stat.metrics.totalExecMicros.minMs} ms" }
                td { classes = tdClasses; +stat.metrics.targetingScore.pretty }
            }
        }
    }
}
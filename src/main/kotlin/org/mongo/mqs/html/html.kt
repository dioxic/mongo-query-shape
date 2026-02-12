package org.mongo.mqs.html

import kotlinx.html.*
import org.mongo.mqs.model.QueryStat

fun HTML.tableHtml(
    queryStats: List<QueryStat>,
    execCount: Boolean = true,
    avgExec: Boolean = true,
    maxExec: Boolean = true,
    minExec: Boolean = true,
    targetScore: Boolean = true
) {
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
                attributes["hx-include"] = "#column-selectors"
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
            id = "column-selectors"
            classes = setOf("flex", "space-x-4", "mb-6", "bg-white", "p-4", "rounded-lg", "shadow-sm")
            columnSelector("execCount", "Execution Count", execCount)
            columnSelector("avgExec", "Avg Execution", avgExec)
            columnSelector("maxExec", "Max Execution", maxExec)
            columnSelector("minExec", "Min Execution", minExec)
            columnSelector("targetScore", "Targeting Score", targetScore)
        }
        div {
            id = "stats-table"
            classes = setOf("overflow-x-auto", "bg-white", "rounded-lg", "shadow")
            table {
                classes = setOf("min-w-full", "divide-y", "divide-gray-200")
                queryStatsTable(queryStats, execCount, avgExec, maxExec, minExec, targetScore)
            }
        }
    }
}

fun DIV.columnSelector(name: String, labelText: String, checked: Boolean) {
    label {
        classes = setOf("flex", "items-center", "space-x-2", "cursor-pointer")
        input(type = InputType.checkBox, name = name) {
            classes = setOf("form-checkbox", "h-5", "w-5", "text-blue-600")
            if (checked) attributes["checked"] = "checked"
            attributes["hx-get"] = "/stats"
            attributes["hx-target"] = "#stats-table"
            attributes["hx-include"] = "#column-selectors"
            attributes["hx-trigger"] = "change"
        }
        span {
            classes = setOf("text-gray-700", "font-medium")
            +labelText
        }
    }
}

fun TABLE.queryStatsTable(
    queryStats: List<QueryStat>,
    execCount: Boolean = true,
    avgExec: Boolean = true,
    maxExec: Boolean = true,
    minExec: Boolean = true,
    targetScore: Boolean = true
) {
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
            if (execCount) th { classes = thClasses; +"Execution Count" }
            if (avgExec) th { classes = thClasses; +"Avg Execution" }
            if (maxExec) th { classes = thClasses; +"Max Execution" }
            if (minExec) th { classes = thClasses; +"Min Execution" }
            if (targetScore) th { classes = thClasses; +"Targeting Score" }
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
                if (execCount) td { classes = tdClasses; +stat.metrics.execCount.toString() }
                if (avgExec) td { classes = tdClasses; +"${stat.metrics.totalExecMicros.avgMs(stat)} ms" }
                if (maxExec) td { classes = tdClasses; +"${stat.metrics.totalExecMicros.maxMs} ms" }
                if (minExec) td { classes = tdClasses; +"${stat.metrics.totalExecMicros.minMs} ms" }
                if (targetScore) td { classes = tdClasses; +stat.metrics.targetingScore.pretty }
            }
        }
    }
}
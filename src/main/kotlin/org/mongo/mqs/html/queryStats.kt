package org.mongo.mqs.html

import kotlinx.html.*
import org.mongo.mqs.model.QueryShapeColumnVisibility
import org.mongo.mqs.model.QueryStat

fun HTML.tableHtml(
    queryStats: List<QueryStat>,
    columnVisibility: QueryShapeColumnVisibility = QueryShapeColumnVisibility.Default
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
                id = "csv-download"
                classes = setOf(
                    "px-4", "py-2", "bg-green-600", "text-white",
                    "rounded-lg", "hover:bg-green-700", "transition-colors", "duration-200"
                )
                +"Download CSV"
            }
            script {
                unsafe {
                    +"""
                        document.addEventListener('htmx:configRequest', (event) => {
                            if (event.detail.target.id === 'stats-table') {
                                const params = new URLSearchParams(new FormData(document.getElementById('column-selectors-form')));
                                document.getElementById('csv-download').href = '/csv?' + params.toString();
                            }
                        });
                        
                        // Initial update for the link
                        function updateCsvLink() {
                             const form = document.getElementById('column-selectors-form');
                             if (form) {
                                 const params = new URLSearchParams(new FormData(form));
                                 document.getElementById('csv-download').href = '/csv?' + params.toString();
                             }
                        }
                        
                        document.addEventListener('change', (event) => {
                            if (event.target.closest('#column-selectors')) {
                                updateCsvLink();
                            }
                        });
                    """.trimIndent()
                }
            }
        }
        form {
            id = "column-selectors-form"
            div {
                id = "column-selectors"
                classes = setOf("flex", "space-x-4", "mb-6", "bg-white", "p-4", "rounded-lg", "shadow-sm")
                columnSelector("execCount", "Execution Count", columnVisibility.execCount)
                columnSelector("avgExec", "Avg Execution", columnVisibility.avgExec)
                columnSelector("maxExec", "Max Execution", columnVisibility.maxExec)
                columnSelector("minExec", "Min Execution", columnVisibility.minExec)
                columnSelector("collScan", "Collection Scan", columnVisibility.collScan)
                columnSelector("targetScore", "Targeting Score", columnVisibility.targetScore)
            }
        }
        div {
            id = "stats-table"
            classes = setOf("overflow-x-auto", "bg-white", "rounded-lg", "shadow")
            table {
                classes = setOf("min-w-full", "divide-y", "divide-gray-200")
                queryStatsTable(queryStats, columnVisibility)
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

fun List<QueryStat>.toRowMap(columnVisibility: QueryShapeColumnVisibility = QueryShapeColumnVisibility.Default): List<Map<String, String>> =
    map { stat ->
        buildMap {
            put("Query Shape Hash", stat.shortHash)
            put("Namespace", "${stat.key.queryShape.cmdNs.db}.${stat.key.queryShape.cmdNs.coll}")
            put("Command", stat.key.queryShape.command)
            put("Query", stat.key.queryShape.query())
            if (columnVisibility.execCount) put("Execution Count", stat.metrics.execCount.toString())
            if (columnVisibility.avgExec) put("Avg Execution", "${stat.metrics.totalExecMicros.avgMs(stat)} ms")
            if (columnVisibility.maxExec) put("Max Execution", "${stat.metrics.totalExecMicros.maxMs} ms")
            if (columnVisibility.minExec) put("Min Execution", "${stat.metrics.totalExecMicros.minMs} ms")
            if (columnVisibility.collScan) put("Collection Scan", stat.metrics.collScan.pretty)
            if (columnVisibility.targetScore) put("Targeting Score", stat.metrics.targetingScore.pretty)
        }
    }

fun TABLE.queryStatsTable(
    queryStats: List<QueryStat>,
    columnVisibility: QueryShapeColumnVisibility,
) {
    genericTable(
        headers = columnVisibility.filter(QueryStat.HEADERS),
        rowMap = queryStats.toRowMap(columnVisibility)
    )
}
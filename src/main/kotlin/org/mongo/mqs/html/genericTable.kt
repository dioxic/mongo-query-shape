package org.mongo.mqs.html

import kotlinx.html.TABLE
import kotlinx.html.classes
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

fun TABLE.genericTable(
    headers: List<String>,
    rowMap: List<Map<String, String>>,
) {
    thead {
        classes = setOf("bg-gray-50")
        tr {
            val thClasses = setOf(
                "px-6", "py-3", "text-left", "text-xs", "font-medium",
                "text-gray-500", "uppercase", "tracking-wider"
            )
            headers.forEach { header -> th { classes = thClasses; +header } }
        }
    }
    tbody {
        classes = setOf("bg-white", "divide-y", "divide-gray-200")
        rowMap.forEach { stat ->
            tr {
                val tdClasses = setOf("px-6", "py-4", "whitespace-nowrap", "text-sm", "text-gray-900")
                headers.forEach { header ->
                    td { classes = tdClasses; +(stat[header] ?: "") }
                }
            }
        }
    }
}
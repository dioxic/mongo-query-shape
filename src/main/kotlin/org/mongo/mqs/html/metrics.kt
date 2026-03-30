package org.mongo.mqs.html

import kotlinx.html.*

import org.mongo.mqs.model.MetricStats

fun HTML.metricsHtml(
    org: String? = "5a05659cd383ad74f1cc1047",
    project: String? = "5a056765c0c6e33bd1ac0cdf",
    cluster: String? = "Cluster1",
    instance: String? = "cluster1-shard-00-01.bj7ub.mongodb.net:27018",
    start: String? = null,
    end: String? = null,
    metrics: List<MetricStats> = emptyList()
) {
    head {
        title { +"Metrics" }
        script { src = "https://cdn.tailwindcss.com" }
    }
    body {
        classes = setOf("bg-gray-100", "p-8", "font-sans")
        h1 {
            classes = setOf("text-3xl", "font-bold", "mb-6", "text-gray-800")
            +"Metrics"
        }

        form(action = "/metrics", method = FormMethod.get) {
            classes = setOf("grid", "grid-cols-2", "gap-4", "mb-8", "bg-white", "p-6", "rounded-lg", "shadow-sm")
            
            div {
                label {
                    classes = setOf("block", "text-sm", "font-medium", "text-gray-700")
                    +"Org"
                }
                input(type = InputType.text, name = "org") {
                    classes = setOf("mt-1", "block", "w-full", "rounded-md", "border-gray-300", "shadow-sm", "focus:border-indigo-500", "focus:ring-indigo-500", "sm:text-sm", "border", "p-2")
                    org?.let { value = it }
                }
            }
            div {
                label {
                    classes = setOf("block", "text-sm", "font-medium", "text-gray-700")
                    +"Project"
                }
                input(type = InputType.text, name = "project") {
                    classes = setOf("mt-1", "block", "w-full", "rounded-md", "border-gray-300", "shadow-sm", "focus:border-indigo-500", "focus:ring-indigo-500", "sm:text-sm", "border", "p-2")
                    project?.let { value = it }
                }
            }
            div {
                label {
                    classes = setOf("block", "text-sm", "font-medium", "text-gray-700")
                    +"Cluster"
                }
                input(type = InputType.text, name = "cluster") {
                    classes = setOf("mt-1", "block", "w-full", "rounded-md", "border-gray-300", "shadow-sm", "focus:border-indigo-500", "focus:ring-indigo-500", "sm:text-sm", "border", "p-2")
                    cluster?.let { value = it }
                }
            }
            div {
                label {
                    classes = setOf("block", "text-sm", "font-medium", "text-gray-700")
                    +"Instance"
                }
                input(type = InputType.text, name = "instance") {
                    classes = setOf("mt-1", "block", "w-full", "rounded-md", "border-gray-300", "shadow-sm", "focus:border-indigo-500", "focus:ring-indigo-500", "sm:text-sm", "border", "p-2")
                    instance?.let { value = it }
                }
            }
            div {
                label {
                    classes = setOf("block", "text-sm", "font-medium", "text-gray-700")
                    +"Start"
                }
                input(type = InputType.dateTimeLocal, name = "start") {
                    classes = setOf("mt-1", "block", "w-full", "rounded-md", "border-gray-300", "shadow-sm", "focus:border-indigo-500", "focus:ring-indigo-500", "sm:text-sm", "border", "p-2")
                    start?.let { value = it }
                }
            }
            div {
                label {
                    classes = setOf("block", "text-sm", "font-medium", "text-gray-700")
                    +"End"
                }
                input(type = InputType.dateTimeLocal, name = "end") {
                    classes = setOf("mt-1", "block", "w-full", "rounded-md", "border-gray-300", "shadow-sm", "focus:border-indigo-500", "focus:ring-indigo-500", "sm:text-sm", "border", "p-2")
                    end?.let { value = it }
                }
            }
            
            div(classes = "col-span-2") {
                button(type = ButtonType.submit) {
                    classes = setOf("w-full", "inline-flex", "justify-center", "py-2", "px-4", "border", "border-transparent", "shadow-sm", "text-sm", "font-medium", "rounded-md", "text-white", "bg-indigo-600", "hover:bg-indigo-700", "focus:outline-none", "focus:ring-2", "focus:ring-offset-2", "focus:ring-indigo-500")
                    +"Submit"
                }
            }
        }

        div {
            classes = setOf("overflow-x-auto", "bg-white", "rounded-lg", "shadow")
            table {
                classes = setOf("min-w-full", "divide-y", "divide-gray-200")
                val headers = listOf("org", "project", "cluster", "instance", "avg cpu", "max cpu", "avg disk", "max disk", "queries/s")
                val rowMap = metrics.map {
                    mapOf(
                        "org" to it.org,
                        "project" to it.project,
                        "cluster" to it.cluster,
                        "instance" to it.instance,
                        "avg cpu" to it.avgCpu.toString(),
                        "avg disk" to it.avgDisk.toString(),
                        "queries/s" to it.inserts.toString()
                    )
                }
                genericTable(headers, rowMap)
            }
        }
    }
}

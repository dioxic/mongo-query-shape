package org.mongo.mqs

import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.request.*
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.stream.createHTML
import kotlinx.html.table
import org.mongo.mqs.export.toCsv
import org.mongo.mqs.html.metricsHtml
import org.mongo.mqs.html.queryStatsTable
import org.mongo.mqs.html.tableHtml
import org.mongo.mqs.model.MetricLabels
import org.mongo.mqs.model.QueryShapeColumnVisibility
import org.mongo.mqs.repository.PrometheusRepository
import org.mongo.mqs.repository.QueryStatRepository
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Instant

fun Application.module() {
    val repository = QueryStatRepository()
    val prometheusRepository = PrometheusRepository("http://localhost:9090")

    routing {
        get("/") {
            val queryStats = repository.execute()
            call.respondHtml {
                tableHtml(queryStats)
            }
        }
        get("/stats") {
            val queryStats = repository.execute()
            val columnVisibility = call.queryShapeVisibility

            call.respondText(
                contentType = ContentType.Text.Html,
                text = createHTML().table {
                    queryStatsTable(queryStats, columnVisibility)
                },
            )
        }
        get("/csv") {
            val queryStats = repository.execute()
            val columnVisibility = call.queryShapeVisibility
            val isDefault = call.parameters.isEmpty()

            val text = if (isDefault) {
                queryStats.toCsv()
            } else {
                queryStats.toCsv(columnVisibility)
            }

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(
                    ContentDisposition.Parameters.FileName, "query-stats.csv"
                ).toString()
            )
            call.respondText(
                contentType = ContentType.Text.CSV,
                text = text
            )
        }
        get("/metrics") {
            val org = call.parameters["org"]
            val project = call.parameters["project"]
            val cluster = call.parameters["cluster"]
            val instance = call.parameters["instance"]
            val start = call.parameters["start"]
            val end = call.parameters["end"]

            val labels = MetricLabels(
                organisation = call.parameters["org"],
                project = call.parameters["project"],
                cluster = call.parameters["cluster"],
                instance = call.parameters["instance"]
            )

            val startInstant = start?.takeIf { it.isNotBlank() }?.let {
                try {
                    LocalDateTime.parse(it).toInstant(ZoneOffset.UTC)
                } catch (e: Exception) {
                    null
                }
            }
            val endInstant = end?.takeIf { it.isNotBlank() }?.let {
                try {
                    LocalDateTime.parse(it).toInstant(ZoneOffset.UTC)
                } catch (e: Exception) {
                    null
                }
            }

            val metrics = prometheusRepository.getMetrics(
                labels,
                startInstant?.let { Instant.fromEpochMilliseconds(it.toEpochMilli()) },
                endInstant?.let { Instant.fromEpochMilliseconds(it.toEpochMilli()) }
            )

            call.respondHtml {
                metricsHtml(org, project, cluster, instance, start, end, metrics)
            }
        }
    }
}

val RoutingCall.queryShapeVisibility
    get() = QueryShapeColumnVisibility(
        execCount = parameters["execCount"] == "on",
        avgExec = parameters["avgExec"] == "on",
        maxExec = parameters["maxExec"] == "on",
        minExec = parameters["minExec"] == "on",
        collScan = parameters["collScan"] == "on",
        targetScore = parameters["targetScore"] == "on",
    )
package org.mongo.mqs

import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.stream.createHTML
import kotlinx.html.table
import org.mongo.mqs.export.toCsv
import org.mongo.mqs.html.queryStatsTable
import org.mongo.mqs.html.tableHtml
import org.mongo.mqs.model.QueryShapeColumnVisibility
import org.mongo.mqs.repository.QueryStatRepository

fun Application.module() {
    val repository = QueryStatRepository()

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
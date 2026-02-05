package org.mongo.mqs

import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.html.stream.createHTML
import kotlinx.html.table
import org.mongo.mqs.export.toCsv
import org.mongo.mqs.html.queryStatsTable
import org.mongo.mqs.html.tableHtml
import org.mongo.mqs.repository.QueryStatRepository

fun Application.module() {
    val repository = QueryStatRepository()

    routing {
        get("/") {
            val queryStats = repository.getQueryStats()
            call.respondHtml {
                tableHtml(queryStats)
            }
        }
        get("/stats") {
            val queryStats = repository.getQueryStats()
            call.respondText(
                contentType = ContentType.Text.Html,
                text = createHTML().table {
                    queryStatsTable(queryStats)
                },
            )
        }
        get("/csv") {
            val queryStats = repository.getQueryStats()
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Companion.Attachment.withParameter(
                    ContentDisposition.Parameters.FileName, "query-stats.csv"
                ).toString()
            )
            call.respondText(
                contentType = ContentType.Text.CSV,
                text = queryStats.toCsv()
            )
        }
    }
}
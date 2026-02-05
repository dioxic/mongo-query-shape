package org.mongo.mqs

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.stream.createHTML
import kotlinx.html.table
import org.mongo.mqs.export.toCsv
import org.mongo.mqs.html.queryStatsTable
import org.mongo.mqs.html.tableHtml
import org.mongo.mqs.repository.QueryStatRepository

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

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
                ContentDisposition.Attachment.withParameter(
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

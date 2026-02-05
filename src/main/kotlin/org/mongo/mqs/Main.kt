package org.mongo.mqs

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.html.respondHtml
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.html.stream.createHTML
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
                createHTML().table {
                    queryStatsTable(queryStats)
                },
                io.ktor.http.ContentType.Text.Html
            )
        }
    }
}

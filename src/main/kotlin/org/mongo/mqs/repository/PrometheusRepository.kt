@file:OptIn(ExperimentalSerializationApi::class)

package org.mongo.mqs.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonElement
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@Serializable
data class PrometheusValuesResponse(
    val status: String,
    val data: List<String>,
    val errorType: String? = null,
    val error: String? = null
)

@Serializable
data class PrometheusResponse(
    val status: String,
    val data: PrometheusData? = null,
    val errorType: String? = null,
    val error: String? = null
)


@Serializable
@JsonClassDiscriminator("resultType")
sealed class PrometheusData

@Serializable
@SerialName("matrix")
data class PrometheusMatrixData(
    val result: List<PrometheusResult>
): PrometheusData()

@Serializable
data class PrometheusResult(
    val metric: Map<String, String>,
    val value: List<JsonElement>? = null,
    val values: List<List<JsonElement>>? = null
)

class PrometheusRepository(private val baseUrl: String, engine: HttpClientEngine = CIO.create()) {
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun query(query: String): PrometheusResponse {
        return client.get("$baseUrl/api/v1/query") {
            parameter("query", query)
        }.body()
    }

    suspend fun queryRange(query: String, start: Instant, end: Instant, step: String): PrometheusResponse {
        return client.get("$baseUrl/api/v1/query_range") {
            parameter("query", query)
            parameter("start", start.epochSeconds)
            parameter("end", end.epochSeconds)
            parameter("step", step)
        }.body()
    }

    suspend fun listMetrics(): List<String> {
        val res: PrometheusValuesResponse = client.get("$baseUrl/api/v1/label/__name__/values").body()
        return res.data
    }

    fun close() {
        client.close()
    }
}

suspend fun main() {
    val repo = PrometheusRepository("http://localhost:9090")

    val start = Clock.System.now().minus(24.hours)
    val end = Clock.System.now()
    val res = repo.queryRange("hardware_process_cpu_normalized_user_percent{group_id=\"5a056765c0c6e33bd1ac0cdf\"}",start, end, "60s")

    res.errorType?.let { error(it) }

    if (res.data is PrometheusMatrixData) {
        res.data.result.forEach(::println)
    }


    println(repo.listMetrics())
}
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
import org.mongo.mqs.model.MetricLabels
import org.mongo.mqs.model.MetricStats
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
@SerialName("vector")
data class PrometheusVectorData(
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

    suspend fun getMetrics(
        labels: MetricLabels,
        start: Instant? = null,
        end: Instant? = null
    ): List<MetricStats> {
        val metrics = "__name__=~\"hardware_process_cpu_normalized_user_percent|mongodb_opcounters_insert\""
        val filters = (listOf(metrics) + labels.toList()).joinToString(separator = ",", prefix = "{", postfix = "}")
        
        val res = query(filters)
        
        return listOf(
            MetricStats(
                org = labels.organisation ?: "",
                project = labels.project ?: "",
                cluster = labels.cluster ?: "",
                instance = labels.instance ?: "",
                avgCpu = 4.5,
                avgDisk = 6.7,
                inserts = 1000.0
            )
        )
    }

    fun close() {
        client.close()
    }
}

suspend fun main() {
    val repo = PrometheusRepository("http://localhost:9090")

    val start = Clock.System.now().minus(24.hours)
    val end = Clock.System.now()
    val res = repo.queryRange("hardware_process_cpu_normalized_user_percent{group_id=\"5a05659cd383ad74f1cc1047\"}",start, end, "60s")

    res.errorType?.let { error(it) }

    if (res.data is PrometheusMatrixData) {
        res.data.result.forEach(::println)
    }

    repo.getMetrics(
        labels = MetricLabels(
            organisation = "5a05659cd383ad74f1cc1047",
        )
    ).forEach { println(it) }

//    println(repo.listMetrics())
}
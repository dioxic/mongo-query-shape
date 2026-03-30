package org.mongo.mqs.repository

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.mongo.mqs.model.MetricStats
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class PrometheusRepositoryTest {

    @Test
    fun `test query`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel("""
                    {
                        "status": "success",
                        "data": {
                            "resultType": "vector",
                            "result": [
                                {
                                    "metric": { "name": "test_metric" },
                                    "value": [1612345678, "42"]
                                }
                            ]
                        }
                    }
                """.trimIndent()),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = PrometheusRepository("http://localhost:9090", mockEngine)
        val response = repository.query("test_metric")

        assertEquals("success", response.status)
        val data = response.data as? PrometheusVectorData
        assertEquals(1, data?.result?.size)
        assertEquals("test_metric", data?.result?.get(0)?.metric?.get("name"))
    }

    @Test
    fun `test queryRange`() = runBlocking {
        val mockEngine = MockEngine { request ->
            assertEquals("http://localhost:9090/api/v1/query_range", request.url.toString().split("?")[0])
            assertEquals("test_metric", request.url.parameters["query"])
            assertEquals("1609459200", request.url.parameters["start"])
            assertEquals("1609462800", request.url.parameters["end"])
            assertEquals("1m", request.url.parameters["step"])

            respond(
                content = ByteReadChannel("""
                    {
                        "status": "success",
                        "data": {
                            "resultType": "matrix",
                            "result": [
                                {
                                    "metric": { "name": "test_metric" },
                                    "values": [
                                        [1612345678, "42"],
                                        [1612345738, "43"]
                                    ]
                                }
                            ]
                        }
                    }
                """.trimIndent()),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val repository = PrometheusRepository("http://localhost:9090", mockEngine)
        val response = repository.queryRange(
            "test_metric",
            Instant.parse("2021-01-01T00:00:00Z"),
            Instant.parse("2021-01-01T01:00:00Z"),
            "1m"
        )

        assertEquals("success", response.status)
        val data = response.data as? PrometheusMatrixData
        assertEquals(1, data?.result?.size)
        assertEquals(2, data?.result?.get(0)?.values?.size)
    }
}

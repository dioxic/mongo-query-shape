package org.mongo.mqs.repository

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

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
        assertEquals("vector", response.data?.resultType)
        assertEquals(1, response.data?.result?.size)
        assertEquals("test_metric", response.data?.result?.get(0)?.metric?.get("name"))
    }

    @Test
    fun `test queryRange`() = runBlocking {
        val mockEngine = MockEngine { request ->
            assertEquals("http://localhost:9090/api/v1/query_range", request.url.toString().split("?")[0])
            assertEquals("test_metric", request.url.parameters["query"])
            assertEquals("2021-01-01T00:00:00Z", request.url.parameters["start"])
            assertEquals("2021-01-01T01:00:00Z", request.url.parameters["end"])
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
        val response = repository.queryRange("test_metric", "2021-01-01T00:00:00Z", "2021-01-01T01:00:00Z", "1m")

        assertEquals("success", response.status)
        assertEquals("matrix", response.data?.resultType)
        assertEquals(1, response.data?.result?.size)
        assertEquals(2, response.data?.result?.get(0)?.values?.size)
    }
}

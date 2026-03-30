package org.mongo.mqs.model

import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.Decimal128
import java.time.Instant

data class QueryStat(
    val key: QueryKey,
    val keyHash: String,
    val queryShapeHash: String,
    val metrics: QueryMetrics,
    val asOf: Instant
) {
    companion object {
        val HEADERS = listOf(
            "Query Shape Hash",
            "Namespace",
            "Command",
            "Query",
            "Execution Count",
            "Avg Execution",
            "Max Execution",
            "Min Execution",
            "Collection Scan",
            "Targeting Score"
        )
    }
}

data class QueryKey(
    val queryShape: QueryShape,
    val collectionType: String,
    val cursor: CursorInfo? = null
)

data class QueryShape(
    val cmdNs: CommandNamespace,
    val command: String,
    // find
    val filter: Document? = null,
    val sort: Document? = null,
    val projection: Document? = null,
    val skip: String? = null,
    val limit: String? = null,
    // aggregation
    val pipeline: List<Document>? = null,
    // distinct & count
    val query: Document? = null,
)

data class CommandNamespace(
    val db: String,
    val coll: String
)

data class CursorInfo(
    val batchSize: String // "?number" in example
)

data class QueryMetrics(
    val lastExecutionMicros: Long,
    val execCount: Long,
    val totalExecMicros: QueryMetricStats,
    val firstResponseExecMicros: QueryMetricStats,
    val docsReturned: QueryMetricStats,
    val keysExamined: QueryMetricStats,
    val docsExamined: QueryMetricStats,
    val bytesRead: QueryMetricStats,
    val readTimeMicros: QueryMetricStats,
    val workingTimeMillis: QueryMetricStats,
    val hasSortStage: BooleanStats,
    val usedDisk: BooleanStats,
    val fromMultiPlanner: BooleanStats,
    val fromPlanCache: BooleanStats,
    val firstSeenTimestamp: Instant,
    val latestSeenTimestamp: Instant
) {
    val targetingScore: Double
        get() = docsExamined.sum.toDouble() / docsReturned.sum.toDouble()

    val collScan: Boolean
        get() = keysExamined.sum == 0L && docsExamined.sum > 0L
}

data class QueryMetricStats(
    val sum: Long,
    val max: Long,
    val min: Long,
    val sumOfSquares: Decimal128? = null
)

data class BooleanStats(
    @BsonProperty("true")
    val trueCount: Long,
    @BsonProperty("false")
    val falseCount: Long
)

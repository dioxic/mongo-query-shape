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
)

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
    val skip: Int? = null,
    val limit: Int? = null,
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
    val totalExecMicros: MetricStats,
    val firstResponseExecMicros: MetricStats,
    val docsReturned: MetricStats,
    val keysExamined: MetricStats,
    val docsExamined: MetricStats,
    val bytesRead: MetricStats,
    val readTimeMicros: MetricStats,
    val workingTimeMillis: MetricStats,
    val hasSortStage: BooleanStats,
    val usedDisk: BooleanStats,
    val fromMultiPlanner: BooleanStats,
    val fromPlanCache: BooleanStats,
    val firstSeenTimestamp: Instant,
    val latestSeenTimestamp: Instant
)

data class MetricStats(
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

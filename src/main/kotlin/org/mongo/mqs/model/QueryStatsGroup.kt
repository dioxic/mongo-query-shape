package org.mongo.mqs.model

import org.bson.codecs.pojo.annotations.BsonId

data class QueryStatsGroup(
    @BsonId
    val id: String,
    val queryStats: List<QueryStat>
)

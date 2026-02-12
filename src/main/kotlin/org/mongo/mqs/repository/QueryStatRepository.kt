package org.mongo.mqs.repository

import com.mongodb.client.model.Aggregates.match
import com.mongodb.client.model.Aggregates.sort
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.`in`
import com.mongodb.client.model.Filters.nin
import com.mongodb.client.model.Sorts
import org.bson.Document
import org.bson.conversions.Bson
import org.mongo.mqs.db.MongoClientProvider
import org.mongo.mqs.model.QueryStat
import org.mongo.mqs.model.QueryStatsGroup

class QueryStatRepository {
    private val database = MongoClientProvider.client.getDatabase("admin")

    private fun queryStats(): Bson {
        return Document($$"$queryStats", Document())
    }

    fun execute(): List<QueryStat> {
        val pipeline = listOf(
            queryStats(),
            match(nin("key.queryShape.cmdNs.db", listOf("config", "local", "admin"))),
            sort(Sorts.ascending("key.queryShapeHash"))
        )
        return database.aggregate(pipeline, QueryStat::class.java).toList()
    }

    fun save(queryStatsGroup: QueryStatsGroup) = database.getCollection<QueryStatsGroup>("queryStats")
        .insertOne(queryStatsGroup)

    fun get(id: String) = database.getCollection<QueryStatsGroup>("queryStats")
        .find(eq("_id", id))
        .firstOrNull()

    fun find(ids: List<String>? = null) =
        database.getCollection<QueryStatsGroup>("queryStats")
            .run {
                if (ids != null) find(`in`("_id", ids)) else find()
            }


}

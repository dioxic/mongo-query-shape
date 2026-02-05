package org.mongo.mqs.html

import org.bson.Document
import org.bson.json.JsonWriterSettings
import org.mongo.mqs.model.QueryShape

private val defaultJws = JsonWriterSettings.builder()
    .indent(true)
    .build()

fun QueryShape.prettyQuery(): String {
    return pipeline?.toJson()
        ?: filter?.toJson(defaultJws)
        ?: query?.toJson(defaultJws)
        ?: "Not Found!"
}

fun List<Document>.toJson(jws: JsonWriterSettings = defaultJws) =
    joinToString(separator = "\n", prefix = "[", postfix = "]") { it.toJson(jws) }
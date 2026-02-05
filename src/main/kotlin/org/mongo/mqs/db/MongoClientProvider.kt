package org.mongo.mqs.db

import com.mongodb.kotlin.client.MongoClient

object MongoClientProvider {
    val client: MongoClient by lazy {
        val connectionString = System.getProperty("mongodb.uri") ?: "mongodb://localhost:27017"
        MongoClient.create(connectionString)
    }
}

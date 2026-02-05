package org.mongo.mqs.model

import com.mongodb.MongoClientSettings
import org.bson.codecs.DecoderContext
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.json.JsonReader
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class QueryStatTest {

    @Test
    fun testDeserialization() {
        val json = String(Files.readAllBytes(Paths.get("src/main/resources/queryStat-example.json")))

        val pojoCodecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        )

        val codec = pojoCodecRegistry.get(QueryStat::class.java)
        val reader = JsonReader(json)
        val queryStat = codec.decode(reader, DecoderContext.builder().build())

        assertNotNull(queryStat)
        assertEquals("local", queryStat.key.queryShape.cmdNs.db)
        assertEquals("oplog.rs", queryStat.key.queryShape.cmdNs.coll)
        assertEquals("aggregate", queryStat.key.queryShape.command)
        assertEquals(19L, queryStat.metrics.execCount)
        assertEquals(0L, queryStat.metrics.hasSortStage.trueCount)
        assertEquals(19L, queryStat.metrics.hasSortStage.falseCount)
    }
}
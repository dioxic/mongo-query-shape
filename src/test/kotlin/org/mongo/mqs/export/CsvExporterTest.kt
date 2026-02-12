package org.mongo.mqs.export

import com.mongodb.MongoClientSettings
import org.bson.codecs.DecoderContext
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.json.JsonReader
import org.mongo.mqs.model.QueryStat
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertTrue

class CsvExporterTest {

    @Test
    fun testToCsv() {
        val json = String(Files.readAllBytes(Paths.get("src/main/resources/queryStat-example.json")))

        val pojoCodecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        )

        val codec = pojoCodecRegistry.get(QueryStat::class.java)
        val reader = JsonReader(json)
        val queryStat = codec.decode(reader, DecoderContext.builder().build())

        val csv = listOf(queryStat).toCsv()

        // Check header
        assertTrue(csv.startsWith("hash,namespace,command,query,execCount,avgMs,maxMs,minMs"))
        
        // Check data row
        assertTrue(csv.contains(queryStat.queryShapeHash))
        assertTrue(csv.contains("local.oplog.rs"))
        assertTrue(csv.contains("aggregate"))
        assertTrue(csv.contains("19")) // execCount
    }

    @Test
    fun testToCsvWithSelectedColumns() {
        val json = String(Files.readAllBytes(Paths.get("src/main/resources/queryStat-example.json")))

        val pojoCodecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        )

        val codec = pojoCodecRegistry.get(QueryStat::class.java)
        val reader = JsonReader(json)
        val queryStat = codec.decode(reader, DecoderContext.builder().build())

        // Only hash, namespace, command, query and execCount
        val csv = listOf(queryStat).toCsv(
            execCount = true,
            avgExec = false,
            maxExec = false,
            minExec = false,
            targetScore = false
        )

        // Check header
        val lines = csv.split("\n")
        kotlin.test.assertEquals("hash,namespace,command,query,execCount", lines[0])

        // Check data row
        assertTrue(lines[1].startsWith(queryStat.queryShapeHash))
        assertTrue(lines[1].contains("19"))
        kotlin.test.assertFalse(lines[1].contains("ms")) // Should not contain ms values if avg/max/min are false
    }
}

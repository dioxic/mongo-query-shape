package org.mongo.mqs.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import org.mongo.mqs.export.toCsv
import org.mongo.mqs.repository.QueryStatRepository

class Csv : CliktCommand(name = "csv") {
    private val output by option("-o", "--output", help = "Output file path (defaults to stdout)").file()

    override fun run() {
        val repository = QueryStatRepository()
        val queryStats = repository.execute()
        val csvContent = queryStats.toCsv()

        if (output != null) {
            output!!.writeText(csvContent)
            echo("CSV exported to ${output!!.absolutePath}")
        } else {
            echo(csvContent)
        }
    }
}
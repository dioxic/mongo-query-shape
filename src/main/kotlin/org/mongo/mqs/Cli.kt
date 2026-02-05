package org.mongo.mqs

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import org.mongo.mqs.cli.Csv
import org.mongo.mqs.cli.Gui

class Cli : CliktCommand() {
    override fun run() = Unit
}

fun main(args: Array<String>) = Cli()
    .subcommands(Gui(), Csv())
    .main(args)


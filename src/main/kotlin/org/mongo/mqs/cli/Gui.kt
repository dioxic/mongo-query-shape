package org.mongo.mqs.cli

import com.github.ajalt.clikt.core.CliktCommand
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.mongo.mqs.module

class Gui : CliktCommand(name = "gui") {
    override fun run() {
        embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
            .start(wait = true)
    }
}
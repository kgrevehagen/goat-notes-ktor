package com.kgrevehagen.goatnotes

import com.kgrevehagen.goatnotes.di.configureKoin
import com.kgrevehagen.goatnotes.plugins.configureSerialization
import com.kgrevehagen.goatnotes.plugins.configureShutdown
import com.kgrevehagen.goatnotes.plugins.configureRouting
import com.kgrevehagen.goatnotes.plugins.configureSecurity
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSecurity()
    configureKoin()
    configureSerialization()
    configureRouting()
    configureShutdown()
}

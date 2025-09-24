package com.kgrevehagen.goatnotes.plugins

import com.kgrevehagen.goatnotes.db.DynamoDbConfig
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import org.koin.ktor.ext.inject

fun Application.configureShutdown() {
    val dynamoDbConfig by inject<DynamoDbConfig>()

    monitor.subscribe(ApplicationStopped) {
        dynamoDbConfig.dynamoDbClient.close()
    }
}
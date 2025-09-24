package com.kgrevehagen.goatnotes.di

import aws.smithy.kotlin.runtime.ExperimentalApi
import com.kgrevehagen.goatnotes.db.DevDynamoDbConfig
import com.kgrevehagen.goatnotes.db.DynamoDbConfig
import com.kgrevehagen.goatnotes.db.ProdDynamoDbConfig
import com.kgrevehagen.goatnotes.db.util.DynamoItemMapper
import com.kgrevehagen.goatnotes.notes.service.DefaultNotesService
import com.kgrevehagen.goatnotes.notes.service.NotesService
import com.kgrevehagen.goatnotes.notes.model.NoteEntity
import com.kgrevehagen.goatnotes.notes.repository.DynamoDbClientNotesRepository
import com.kgrevehagen.goatnotes.notes.repository.NotesRepository
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
        modules(dbModule)
        modules(module { single { environment.config } }
        )
    }
}

@OptIn(ExperimentalApi::class)
val appModule = module {
    singleOf(::DynamoDbClientNotesRepository) bind NotesRepository::class
    singleOf(::DefaultNotesService) bind NotesService::class
    single { NoteEntity.Factory() }
}

@OptIn(ExperimentalApi::class)
val dbModule = module {
    single<DynamoDbConfig> {
        when (System.getProperty("env")) {
            "prod" -> ProdDynamoDbConfig(get())
            else -> DevDynamoDbConfig(get())
        }
    }
    single { get<DynamoDbConfig>().dynamoDbMapper }
    single { get<DynamoDbConfig>().dynamoDbClient }
    single { DynamoItemMapper() }
}

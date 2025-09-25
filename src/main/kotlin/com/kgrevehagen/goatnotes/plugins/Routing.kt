package com.kgrevehagen.goatnotes.plugins

import com.kgrevehagen.goatnotes.notes.service.NotesService
import com.kgrevehagen.goatnotes.notes.routing.notesRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    install(Resources)
    install(CallLogging)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to cause.message))
        }
        exception<NoSuchElementException> { call, cause ->
            cause.printStackTrace()
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
    }

    val notesService by inject<NotesService>()

    routing {
        notesRoutes(notesService)
    }
}

package com.kgrevehagen.goatnotes.plugins

import com.kgrevehagen.goatnotes.notes.routing.notesRoutes
import com.kgrevehagen.goatnotes.notes.routing.uiNotesRoutes
import com.kgrevehagen.goatnotes.notes.service.NotesService
import freemarker.cache.ClassTemplateLoader
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

private val logger = KotlinLogging.logger {}

fun Application.configureRouting() {
    install(Resources)
    install(CallLogging)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logger.error(cause) { "Unhandled exception: ${cause.message}" }
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to cause.message))
        }
        exception<NoSuchElementException> { call, cause ->
            logger.warn(cause) { "Not found: ${cause.message}" }
            call.respond(HttpStatusCode.NotFound, mapOf("error" to cause.message))
        }
    }
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    val notesService by inject<NotesService>()

    routing {
        notesRoutes(notesService)
        uiNotesRoutes(notesService)
    }
}

suspend fun ApplicationCall.withUserIdOrForbidden(block: suspend (userId: String) -> Unit) {
    val principal = principal<JWTPrincipal>()
    principal?.subject?.let { userId ->
        block(userId)
    } ?: respond(HttpStatusCode.Forbidden)
}

suspend fun ApplicationCall.withPrincipalOrForbidden(block: suspend (principal: JWTPrincipal) -> Unit) {
    val principal = principal<JWTPrincipal>()
    principal?.let { principal ->
        block(principal)
    } ?: respond(HttpStatusCode.Forbidden)
}
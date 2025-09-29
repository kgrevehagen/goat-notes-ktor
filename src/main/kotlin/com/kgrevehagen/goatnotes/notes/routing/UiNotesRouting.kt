package com.kgrevehagen.goatnotes.notes.routing

import com.kgrevehagen.goatnotes.notes.model.CreateNoteRequest
import com.kgrevehagen.goatnotes.notes.service.NotesService
import com.kgrevehagen.goatnotes.plugins.withPrincipalOrForbidden
import com.kgrevehagen.goatnotes.plugins.withUserIdOrForbidden
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.date.GMTDate

internal fun Route.uiNotesRoutes(notesService: NotesService) {
    install(DoubleReceive)

    authenticate("oauth", "oauth-jwt") {
        get("/oauth2/callback") {
            call.principal<OAuthAccessTokenResponse.OAuth2>()?.let { token ->
                call.response.cookies.append(
                    Cookie(name = "access_token", value = token.accessToken, path = "/", httpOnly = true, maxAge = 3600)
                )

                val redirectUri = call.request.cookies["origin_uri"] ?: "/"
                call.response.cookies.append("origin_uri", "", expires = GMTDate.START)
                call.respondRedirect(redirectUri)
            } ?: run {
                call.respondText("Authentication failed")
            }
        }

        route("/ui/notes") {
            get {
                call.withPrincipalOrForbidden {
                    it.subject?.let { userId ->
                        val notes = notesService.getNotesForUser(userId, 50, null).items
                        val name = it.getClaim("preferred_username", String::class) ?: userId

                        call.respond(
                            FreeMarkerContent(
                                "notes.ftl",
                                mapOf("notes" to notes, "name" to name)
                            )
                        )
                    } ?: call.respond(HttpStatusCode.NotFound)
                }
            }

            post {
                call.withUserIdOrForbidden {
                    val params = call.receiveParameters()
                    val action = params["action"]
                    when (action) {
                        "create" -> {
                            val noteText = params["noteText"] ?: ""
                            if (noteText.isNotBlank()) {
                                notesService.createNoteForUser(it, CreateNoteRequest(noteText))
                            }
                        }

                        "delete" -> {
                            val noteId = params["noteId"] ?: ""
                            if (noteId.isNotBlank()) {
                                notesService.deleteNoteForUser(it, noteId)
                            }
                        }
                    }
                    call.respondRedirect("/ui/notes")
                }
            }
        }

        post("/logout") {
            call.response.cookies.append("access_token", "", expires = GMTDate.START) // clean up
            val logoutUrl = environment.config.property("oauth.logoutUrl").getString()
            call.respondRedirect(logoutUrl)
        }
    }
}
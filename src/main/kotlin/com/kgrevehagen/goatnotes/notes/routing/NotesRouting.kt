package com.kgrevehagen.goatnotes.notes.routing

import com.kgrevehagen.goatnotes.notes.model.CreateNoteRequest
import com.kgrevehagen.goatnotes.notes.service.NotesService
import com.kgrevehagen.goatnotes.plugins.withUserIdOrForbidden
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

internal fun Route.notesRoutes(notesService: NotesService) {
    authenticate {
        get<NotesResource.WithPaging> {
            call.withUserIdOrForbidden { userId ->
                val notes = notesService.getNotesForUser(userId, it.limit, it.exclusiveStartKey)
                call.respond(notes)
            }
        }

        get<NotesResource.ById> {
            call.withUserIdOrForbidden { userId ->
                val note = notesService.getNoteForUser(userId, it.id)
                call.respond(note)
            }
        }

        post<NotesResource> {
            call.withUserIdOrForbidden { userId ->
                val noteRequest = call.receive<CreateNoteRequest>()
                val note = notesService.createNoteForUser(userId, noteRequest)
                val location = "/notes/${note.noteId}"
                call.response.headers.append(HttpHeaders.Location, location)
                call.respond(HttpStatusCode.Created, note)
            }
        }

        delete<NotesResource.ById> {
            call.withUserIdOrForbidden { userId ->
                if (notesService.deleteNoteForUser(userId, it.id)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Note not found"))
                }
            }
        }
    }
}

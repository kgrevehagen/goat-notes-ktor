package com.kgrevehagen.goatnotes.notes.routing

import io.ktor.resources.Resource

@Resource("/notes")
class NotesResource {

    @Resource("")
    data class WithPaging(
        val parent: NotesResource = NotesResource(),
        val limit: Int = 20,
        val exclusiveStartKey: String? = null,
    )

    @Resource("{id}")
    data class ById(val parent: NotesResource = NotesResource(), val id: String)

}

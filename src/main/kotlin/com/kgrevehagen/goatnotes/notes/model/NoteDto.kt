package com.kgrevehagen.goatnotes.notes.model

import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    var userId: String,
    var noteId: String,
    var noteText: String,
    var createdAt: Long,
)

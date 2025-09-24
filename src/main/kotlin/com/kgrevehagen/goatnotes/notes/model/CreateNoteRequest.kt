package com.kgrevehagen.goatnotes.notes.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CreateNoteRequest(val noteText: String)

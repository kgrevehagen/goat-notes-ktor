package com.kgrevehagen.goatnotes.notes.model

import kotlinx.serialization.Serializable

@Serializable
data class NotePrimaryKey(val userId: String, val noteId: String)
package com.kgrevehagen.goatnotes.notes.repository

import com.kgrevehagen.goatnotes.model.PageInfo
import com.kgrevehagen.goatnotes.notes.model.NoteEntity

internal interface NotesRepository {
    suspend fun createNoteForUser(noteEntity: NoteEntity)
    suspend fun getNotesForUser(userId: String, limit: Int, exclusiveStartKey: String?): PageInfo<NoteEntity>
    suspend fun getNoteForUser(userId: String, noteId: String): NoteEntity
    suspend fun deleteNoteForUser(userId: String, noteId: String): Boolean
}

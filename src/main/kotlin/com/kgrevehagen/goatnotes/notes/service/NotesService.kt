package com.kgrevehagen.goatnotes.notes.service

import com.kgrevehagen.goatnotes.model.PageInfo
import com.kgrevehagen.goatnotes.notes.model.CreateNoteRequest
import com.kgrevehagen.goatnotes.notes.model.NoteDto
import com.kgrevehagen.goatnotes.notes.model.NoteEntity
import com.kgrevehagen.goatnotes.notes.repository.NotesRepository
import java.util.UUID

internal interface NotesService {
    suspend fun createNoteForUser(userId: String, noteRequest: CreateNoteRequest): NoteDto
    suspend fun getNotesForUser(userId: String, limit: Int, exclusiveStartKey: String?): PageInfo<NoteDto>
    suspend fun getNoteForUser(userId: String, noteId: String): NoteDto
    suspend fun deleteNoteForUser(userId: String, noteId: String): Boolean
}

internal class DefaultNotesService(
    private val notesRepository: NotesRepository,
    private val noteEntityFactory: NoteEntity.Factory,
) : NotesService {

    override suspend fun createNoteForUser(userId: String, noteRequest: CreateNoteRequest): NoteDto {
        val noteEntity = noteEntityFactory.create(
            userId,
            UUID.randomUUID().toString(),
            noteRequest.noteText,
            System.currentTimeMillis()
        )
        notesRepository.createNoteForUser(noteEntity)
        return noteEntity.toDto()
    }

    override suspend fun getNotesForUser(userId: String, limit: Int, exclusiveStartKey: String?): PageInfo<NoteDto> {
        return notesRepository.getNotesForUser(userId, limit, exclusiveStartKey).let {
            PageInfo(
                items = it.items.toDto(),
                lastEvaluatedKey = it.lastEvaluatedKey
            )
        }
    }

    override suspend fun getNoteForUser(
        userId: String,
        noteId: String
    ): NoteDto {
        return notesRepository.getNoteForUser(userId, noteId).toDto()
    }

    override suspend fun deleteNoteForUser(userId: String, noteId: String): Boolean {
        return notesRepository.deleteNoteForUser(userId, noteId)
    }
}

private fun NoteEntity.toDto(): NoteDto {
    return NoteDto(userId, noteId, noteText, createdAt)
}

private fun List<NoteEntity>.toDto(): List<NoteDto> {
    return map { it.toDto() }
}

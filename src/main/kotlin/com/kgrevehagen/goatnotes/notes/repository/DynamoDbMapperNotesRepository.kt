package com.kgrevehagen.goatnotes.notes.repository

import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbMapper
import aws.sdk.kotlin.hll.dynamodbmapper.annotations.ManualPagination
import aws.sdk.kotlin.hll.dynamodbmapper.expressions.KeyFilter
import aws.sdk.kotlin.hll.dynamodbmapper.operations.PutItemRequest
import aws.sdk.kotlin.hll.dynamodbmapper.operations.QueryRequest
import aws.sdk.kotlin.hll.dynamodbmapper.operations.deleteItem
import aws.sdk.kotlin.services.dynamodb.model.ReturnValue
import aws.smithy.kotlin.runtime.ExperimentalApi
import com.kgrevehagen.goatnotes.model.PageInfo
import com.kgrevehagen.goatnotes.notes.model.NoteEntity
import com.kgrevehagen.goatnotes.notes.model.USER_ID_CREATED_AT_INDEX_NAME
import com.kgrevehagen.goatnotes.notes.model.dynamodbmapper.generatedschemas.NoteEntitySchema
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

private const val MAX_LIMIT = 20
private const val TABLE_NAME = "Notes"

/**
 * A repository for managing notes in a DynamoDB table using the high-level DynamoDbMapper.
 * It is currently in Developer Preview, with some known issues, so we're not using this for now.
 * It's especially the `exclusiveStartKey` that's causing trouble.
 */
@OptIn(ExperimentalApi::class, ManualPagination::class)
internal class DynamoDbMapperNotesRepository(private val dynamoDbMapper: DynamoDbMapper) : NotesRepository {

    override suspend fun createNoteForUser(noteEntity: NoteEntity) {
        val request = PutItemRequest {
            item = noteEntity
        }

        dynamoDbMapper.getTable(TABLE_NAME, NoteEntitySchema).putItem(request)
    }

    override suspend fun getNotesForUser(
        userId: String,
        limit: Int,
        exclusiveStartKey: String?
    ): PageInfo<NoteEntity> {
        val table = dynamoDbMapper.getTable(TABLE_NAME, NoteEntitySchema)
        val index = table.getIndex(USER_ID_CREATED_AT_INDEX_NAME, NoteEntitySchema)

        val exclusiveStartKeyItem = exclusiveStartKey?.takeIf { it.isNotEmpty() }
            ?.let { Base64.decode(it) }?.decodeToString()
            ?.let { Json.decodeFromString<NoteEntity>(it) }

        val request = QueryRequest {
            keyCondition = KeyFilter(userId)
            this.limit = limit.coerceAtMost(MAX_LIMIT)
            this.exclusiveStartKey = exclusiveStartKeyItem
            scanIndexForward = false
        }

        // TODO there are some issues with the exclusiveStartKey when querying on an index.
        // See here for more info: https://github.com/aws/aws-sdk-kotlin/issues/1677
        val response = index.query(request)

        val items = response.items ?: emptyList()

        val lastEvaluatedKey = response.lastEvaluatedKey?.let { Json.encodeToString(it) }
            ?.encodeToByteArray()?.let { Base64.encode(it) }

        return PageInfo(items, lastEvaluatedKey)
    }

    override suspend fun getNoteForUser(
        userId: String,
        noteId: String
    ): NoteEntity {
        return dynamoDbMapper.getTable(TABLE_NAME, NoteEntitySchema)
            .getItem(userId, noteId)
            ?: throw NoSuchElementException("No note found for userId: $userId and noteId: $noteId")
    }

    override suspend fun deleteNoteForUser(userId: String, noteId: String): Boolean {
        val itemToDelete = getNoteForUser(userId, noteId)
        return dynamoDbMapper.getTable(TABLE_NAME, NoteEntitySchema)
            .deleteItem {
                key = itemToDelete
                returnValues = ReturnValue.AllOld
            }.attributes != null
    }
}

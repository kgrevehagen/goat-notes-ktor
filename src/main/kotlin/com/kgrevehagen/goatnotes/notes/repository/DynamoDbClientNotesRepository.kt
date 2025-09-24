package com.kgrevehagen.goatnotes.notes.repository

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.dynamodb.model.QueryRequest
import aws.sdk.kotlin.services.dynamodb.model.ReturnValue
import com.kgrevehagen.goatnotes.db.util.DynamoItemMapper
import com.kgrevehagen.goatnotes.model.PageInfo
import com.kgrevehagen.goatnotes.notes.model.NoteEntity
import com.kgrevehagen.goatnotes.notes.model.NotePrimaryKey
import com.kgrevehagen.goatnotes.notes.model.USER_ID_CREATED_AT_INDEX_NAME
import kotlin.io.encoding.Base64

private const val MAX_LIMIT = 20
private const val TABLE_NAME = "Notes"

/**
 * A repository for managing notes in a DynamoDB table using the low-level DynamoDbClient.
 */
internal class DynamoDbClientNotesRepository(
    private val dynamoDbClient: DynamoDbClient,
    private val mapper: DynamoItemMapper
) : NotesRepository {

    override suspend fun createNoteForUser(noteEntity: NoteEntity) {
        val request = PutItemRequest {
            tableName = TABLE_NAME
            item = mapper.toDynamoItem(noteEntity)
        }

        dynamoDbClient.putItem(request)
    }

    override suspend fun getNotesForUser(
        userId: String,
        limit: Int,
        exclusiveStartKey: String?,
    ): PageInfo<NoteEntity> {
        val exclusiveStartKeyItem = exclusiveStartKey?.takeIf { it.isNotEmpty() }
            ?.let { Base64.decode(it) }?.decodeToString()
            ?.let { mapper.toDynamoItem(it) }

        val request = QueryRequest {
            tableName = TABLE_NAME
            indexName = USER_ID_CREATED_AT_INDEX_NAME
            keyConditionExpression = "userId = :userId"
            expressionAttributeValues = mapOf(":userId" to AttributeValue.S(userId))
            this.limit = limit.coerceAtMost(MAX_LIMIT)
            this.exclusiveStartKey = exclusiveStartKeyItem
            scanIndexForward = false
        }

        val response = dynamoDbClient.query(request)

        val items = response.items?.map { mapper.toItem<NoteEntity>(it) } ?: emptyList()

        val lastEvaluatedKey = response.lastEvaluatedKey?.let { mapper.toJson(it) }
            ?.encodeToByteArray()?.let { Base64.encode(it) }

        return PageInfo(items, lastEvaluatedKey)
    }

    override suspend fun getNoteForUser(userId: String, noteId: String): NoteEntity {
        val request = GetItemRequest {
            tableName = TABLE_NAME
            key = mapper.toDynamoItem(NotePrimaryKey(userId, noteId))
        }

        return dynamoDbClient.getItem(request).item?.let { mapper.toItem(it) }
            ?: throw NoSuchElementException("Note with id $noteId for user $userId not found")
    }

    override suspend fun deleteNoteForUser(userId: String, noteId: String): Boolean {
        val request = DeleteItemRequest {
            tableName = TABLE_NAME
            key = mapper.toDynamoItem(NotePrimaryKey(userId, noteId))
            returnValues = ReturnValue.AllOld
        }

        val response = dynamoDbClient.deleteItem(request)
        return response.attributes?.isNotEmpty() == true
    }
}

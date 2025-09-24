package com.kgrevehagen.goatnotes.notes.model

import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbItem
import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbPartitionKey
import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbSortKey
import kotlinx.serialization.Serializable

@Serializable
@DynamoDbItem
data class NoteEntity(
    @DynamoDbPartitionKey
    var userId: String,
    @DynamoDbSortKey
    var noteId: String,
    var noteText: String,
    var createdAt: Long,
) {
    constructor() : this("", "", "", 0)

    class Factory {
        fun create(userId: String, noteId: String, noteText: String, createdAt: Long): NoteEntity {
            return NoteEntity(userId, noteId, noteText, createdAt)
        }
    }
}

internal const val USER_ID_CREATED_AT_INDEX_NAME = "userId-createdAt-index"

package com.kgrevehagen.goatnotes.db

import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbMapper
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.smithy.kotlin.runtime.ExperimentalApi

internal interface DynamoDbConfig {
    val dynamoDbClient: DynamoDbClient

    @OptIn(ExperimentalApi::class)
    val dynamoDbMapper: DynamoDbMapper
}


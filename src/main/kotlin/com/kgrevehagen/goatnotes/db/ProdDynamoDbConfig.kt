package com.kgrevehagen.goatnotes.db

import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbMapper
import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.smithy.kotlin.runtime.ExperimentalApi
import io.ktor.server.config.ApplicationConfig

internal class ProdDynamoDbConfig(private val config: ApplicationConfig) : DynamoDbConfig {

    override val dynamoDbClient = DynamoDbClient {
        region = config.property("aws.dynamodb.region").getString()
        credentialsProvider =
            ProfileCredentialsProvider(profileName = config.property("aws.profileName").getString())
    }

    @OptIn(ExperimentalApi::class)
    override val dynamoDbMapper = DynamoDbMapper(dynamoDbClient)
}